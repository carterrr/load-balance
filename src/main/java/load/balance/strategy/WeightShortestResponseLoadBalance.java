package load.balance.strategy;

import load.balance.AbstractLoadBalance;
import load.balance.LoadBalance;
import load.balance.Node;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加权最短响应时间
 */
public class WeightShortestResponseLoadBalance extends AbstractLoadBalance {

    protected WeightShortestResponseLoadBalance(List<Node> nodes) {
        super(nodes);
    }

    @Override
    public Node select(String request) {
        int shortest = -1;
        int shortestCount = 0;
        int[] shortestIdxes = new int[nodes.size()];
        int totalWeight = 0;
        int firstWeight = 0;
        boolean sameWeight = true;
        for (int i = 0; i < nodes.size(); i++) {
            int curRt = nodes.get(i).getCurRt();
            int weight = nodes.get(i).getWeight();
            if(shortest == -1 || (curRt < shortest)) {
                shortest = curRt;
                shortestCount = 1;
                totalWeight = weight;
                firstWeight = weight;
                shortestIdxes[0] = i;
            } else if (curRt == shortest) {
                shortestIdxes[shortestCount++] = i;
                totalWeight += weight;
                sameWeight &= (weight == firstWeight);
            }
        }

        if (shortestCount == 1) return nodes.get(shortestIdxes[0]);
        // 加权随机
        if( !sameWeight ) {
            int rnd = ThreadLocalRandom.current().nextInt(totalWeight);
            for (int i = 0; i < shortestCount; i++) {
                Node node = nodes.get(shortestIdxes[i]);
                rnd -= node.getWeight();
                if (rnd < 0) return node;
            }
        }
        // 完全随机
        return nodes.get(shortestIdxes[ThreadLocalRandom.current().nextInt(shortestCount)]);
    }
}
