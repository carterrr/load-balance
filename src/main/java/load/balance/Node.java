package load.balance;

public class Node {
    int idx;
    String address;
    int weight;
    int curActive;
    int curRt;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getCurActive() {
        return curActive;
    }

    public void setCurActive(int curActive) {
        this.curActive = curActive;
    }

    public int getCurRt() {
        return curRt;
    }

    public void setCurRt(int curRt) {
        this.curRt = curRt;
    }
}
