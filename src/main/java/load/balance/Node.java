package load.balance;

public class Node {
    int idx;
    String address;
    int weight;
    int curActive;
    int curRt;
    EWMAActive ewmaActive = new EWMAActive(100, 100);


    public void active() {
        this.curActive ++;
        this.ewmaActive.active();
    }

    public double getEWMAActive() {
        return this.ewmaActive.avg;
    }

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



    /**
     * twitter使用移动加权平均  https://linkerd.io/1/features/load-balancing/#power-of-two-choices-p2c-least-loaded
     * 最小连接数的变体  考虑负载波动来 求每个node的连接数负载
     * 移动加权平均 EWMA算法  具体细节参考链接 http://www.javashuo.com/article/p-gqjpzres-s.html
     *
     */

    public static class EWMAActive {
        private boolean sliding = false;
        private long window;
        private long betaWindow;
        private double beta = -1D;
        private long last;
        private double avg;

        public double getAvg() {
            return avg;
        }

        public EWMAActive(long window, long betaWindow) {
            this.window = window;
            this.betaWindow = betaWindow;  // 用来计算初始beta值
            this.sliding = true;
        }

        public void active() {
            active(System.currentTimeMillis());
        }

        public void active(long millis) {
            // 重置 超过单个统计窗口
            if(last > 0 && millis - this.last > this.window) {
                this.last = 0;
            }

            if(this.last == 0) {
                this.avg = 0;
                this.last = millis;
            }


            long diff = millis - last;
            // 计算alpha  默认初始值为 Math.exp(-1.0 * ((double) diff/ alphaWindow))
            double beta_ = this.beta != -1D ? this.beta : Math.exp(-1.0 * ((double) diff/ betaWindow));
            // 计算滑动均值
            this.avg = (1.0 - beta_) * diff + beta_ * this.avg;
            this.last = millis;
        }




    }
}
