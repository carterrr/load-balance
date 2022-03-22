package load.balance.strategy;

import load.balance.AbstractLoadBalance;
import load.balance.Node;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private static final int REPLICA_NUMBER = 160; // virtual node count for every real node
    private TreeMap<Long, Node> hashCycle = new TreeMap<>(); // consistent hash cycle


    protected ConsistentHashLoadBalance(List<Node> nodes) {
        super(nodes);
        for (Node node : nodes) {
            String address = node.getAddress();
            for (int i = 0; i < REPLICA_NUMBER / 4; i++) {
                byte[] digest = md5(address + i);
                for (int j = 0; j < 4; j++) {
                    long k = hash(digest, j);
                    hashCycle.put(k, node);
                }
            }
        }
    }


    @Override
    public Node select(String request) {
        byte[] md5 = md5(request);
        Long key = hash(md5, 0);
        Map.Entry<Long, Node> longNodeEntry = hashCycle.ceilingEntry(key);
        if (longNodeEntry == null) {
            longNodeEntry = hashCycle.firstEntry();
        }
        return longNodeEntry.getValue();
    }

    private byte[] md5(String value) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.reset();
        md5.update(value.getBytes(StandardCharsets.UTF_8));
        return md5.digest();
    }

    private long hash(byte[] digest, int number) {
        //number可以是，0,1,2,3 long 类型64 bit位
        //最后0xFFFFFFFFL;保证4字节位表示数值。相当于Ingter型数值。所以hash环的值域是[0,Integer.max_value]
        //每次取digest4个字节（|操作），组成4字节的数值。
        //当number 为 0,1,2,3时，分别对应digest第
        // 1，2,3,4;
        // 5,6,7，8；
        // 9,10,11,12;
        // 13,14,15,16;字节
        //4批
        return (
                (
                        //digest的第4(number 为 0时),8(number 为 1),12(number 为 2),16(number 为 3)字节，&0xFF后，左移24位
                        (long) (digest[3 + number * 4] & 0xFF) << 24
                )
                        |(
                        //digest的第3,7,11,15字节，&0xFF后，左移16位
                        (long) (digest[2 + number * 4] & 0xFF) << 16
                )
                        |(
                        //digest的第2,6,10,14字节，&0xFF后，左移8位
                        (long) (digest[1 + number * 4] & 0xFF) << 8
                )
                        |(
                        //digest的第1,5,9,13字节，&0xFF
                        digest[number * 4] & 0xFF
                )
        )
                & 0xFFFFFFFFL;
    }
}
