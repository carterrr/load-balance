package load.balance.strategy;

import load.balance.AbstractLoadBalance;
import load.balance.Node;

import java.util.List;

/**
 * 加权轮询
 *
 * 有状态版本
 *
 * dubbo中存的是 methodWeightMap 保存 method -> invoker的权重对象
 */
public class WeightRoundRobinLoadBalance extends AbstractLoadBalance {

    private final int[] weights;
    private int totalWeight;
    private final int[] curWeights;
    protected WeightRoundRobinLoadBalance(List<Node> nodes) {
        super(nodes);
        this.totalWeight = 0;
        this.weights = new int[nodes.size()];
        this.curWeights = new int[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            int weight = nodes.get(i).getWeight();
            weights[i] = weight;
            curWeights[i] = weight;
            totalWeight += weight;
        }
    }

    @Override
    public Node select(String request) {
        int targetIdx = 0;
        int maxWeight = Integer.MIN_VALUE;
        int total = 0;
        // find largest
        for (int i = 0; i < curWeights.length; i++) {
            if (curWeights[i] > maxWeight) {
                targetIdx = i;
                maxWeight = curWeights[i];
            }
            total += curWeights[i];
        }
        // decrease the max by total
        curWeights[targetIdx] -= total;

        // plus all
        for (int i = 0; i < curWeights.length; i++) {
            curWeights[i] += weights[i];

        }
        return nodes.get(targetIdx);
    }
}
