package load.balance.strategy;

import load.balance.AbstractLoadBalance;
import load.balance.LoadBalance;
import load.balance.Node;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加权随机
 */
public class WeightRandomLoadBalance extends AbstractLoadBalance {

    private int totalWeight = 0;
    private boolean isSameWeight = true;
    protected WeightRandomLoadBalance(List<Node> nodes) {
        super(nodes);
        int firstWeight = nodes.get(0).getWeight();
        for(Node node : nodes) {
            int weight = node.getWeight();
            totalWeight += weight;
            isSameWeight = isSameWeight && (weight == firstWeight);
        }
    }

    @Override
    public Node select(String request) {
        if(!isSameWeight) {
            int rnd = ThreadLocalRandom.current().nextInt(totalWeight);
            for (Node node : nodes) {
                rnd -= node.getWeight();
                if(rnd <= 0) return node;
            }
        }
        // 其他情况一律随机
        return nodes.get(ThreadLocalRandom.current().nextInt(nodes.size()));
    }
}
