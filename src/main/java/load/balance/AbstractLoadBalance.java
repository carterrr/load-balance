package load.balance;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance{

    protected final List<Node> nodes;

    protected AbstractLoadBalance(List<Node> nodes) {
        this.nodes = nodes;
    }
}
