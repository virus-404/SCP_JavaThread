package eps.scp;
import java.util.*;
import java.util.concurrent.TimeUnit;

class ThreadWorker  extends Thread {
    private static ArrayList<Integer> curr = new ArrayList<Integer>();
    private static List<Integer> prev; //static means shared with the same objects of the same classes
    private static List<Item> itemList;
    private static int idx;
    private static int turn = 0;
    private int start;
    private int stop;
    private int ID;

    ThreadWorker(int start, int stop, int id) {
        this.start = start;
        this.stop = stop;
        this.ID = id;
    }

    @Override
    public void run() {
        int j = start;
        int max = stop;
        List<Integer> res = new ArrayList<Integer>();
        for (; j <= max; j++){
            if (j > 0) {
                int wH = itemList.get(idx - 1).getWeight();
                if (wH > j)
                    res.add(prev.get(j));
                else {
                    res.add(Math.max(prev.get(j), itemList.get(idx - 1).getValue() + prev.get(j - wH)));
                }
            } else {
                res.add(0);
            }
        }

        while (ID != turn){
            try {
                TimeUnit.NANOSECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("[Thread] Access granted by " + ID);
        synchronized (curr){
            for (Integer integer : res) curr.add(integer);
        }
        turn = turn + 1;
        //System.out.println("[Thread] Access freed by " + ID +" to "+ turn);
        super.run();
    }

    protected static void setItemList(List<Item> itemList) {
        ThreadWorker.itemList = itemList;
    }

    protected static void setPrev(List<Integer> prev) {
        ThreadWorker.prev = prev;
    }

    protected static void setIdx(int idx) {
        ThreadWorker.idx = idx;
    }

    protected static List<Integer> getCurr(){
        List<Integer> tmp = curr;
        curr =  new ArrayList<Integer>();
        turn = 0;
        return tmp;
    }

    public int getID() {
        return ID;
    }
}
