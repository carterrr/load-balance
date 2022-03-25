package load.balance.strategy;

import load.balance.AbstractLoadBalance;
import load.balance.Node;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加权滑动计数均值（EWMA）
 */
public class WeightLeastEWMAActiveLoadBalance extends AbstractLoadBalance {



    protected WeightLeastEWMAActiveLoadBalance(List<Node> nodes) {
        super(nodes);
    }

    @Override
    public Node select(String request) {
        double leastActive = -1;
        int[] leastIdx = new int[nodes.size()];
        int leastCount = 0;
        int totalWeight = 0;
        boolean sameWeight = true;
        int firstWeight = 0;
        for (int i = 0; i < nodes.size(); i++) {
            if(leastActive == -1 || nodes.get(i).getEWMAActive() < leastActive) {
                leastIdx[0] = i;
                leastCount = 1;
                int weight = nodes.get(i).getWeight();
                totalWeight = weight;
                firstWeight = weight;
            } else if (leastActive == nodes.get(i).getEWMAActive()) {
                leastIdx[leastCount++] = i;
                int weight = nodes.get(i).getWeight();
                totalWeight += weight;
                sameWeight &= (weight == firstWeight);
            }
        }

        if(leastCount == 1) return nodes.get(leastIdx[0]);

        // 剩下转化为加权随机
        if(!sameWeight) {
            int rnd = ThreadLocalRandom.current().nextInt(totalWeight);
            for(int i = 0; i < leastCount; i++) {
                int leastIdx1 = leastIdx[i];
                rnd -= nodes.get(leastIdx1).getWeight();
                if(rnd < 0) return nodes.get(leastIdx1);
            }
        }

        // 完全随机
        return nodes.get(leastIdx[ThreadLocalRandom.current().nextInt(leastCount)]);
    }
}
