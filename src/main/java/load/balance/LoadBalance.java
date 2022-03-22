package load.balance;

import java.util.List;

public interface LoadBalance {

    Node select(String request);

}
