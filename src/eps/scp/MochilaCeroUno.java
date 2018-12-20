package eps.scp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MochilaCeroUno {
    protected List<Item> itemList  = new ArrayList<Item>();
    protected int maxWeight        = 0;
    protected int solutionWeight   = 0;
    protected int profit           = 0;
    protected boolean calculated   = false;

    int max_thread = MochilaAcotadaSec.getMax_threads();

    public MochilaCeroUno() {}
    public MochilaCeroUno(int _maxWeight) {
        setMaxWeight(_maxWeight);
    }
    public MochilaCeroUno(List<Item> _itemList) {
        setItemList(_itemList);
    }
    public MochilaCeroUno(List<Item> _itemList, int _maxWeight) {
        setItemList(_itemList);
        setMaxWeight(_maxWeight);
    }
    // calculte the solution of 0-1 knapsack problem with dynamic method:

    public List<Item> calcSolution() {
        int n = itemList.size();
        setInitialStateForCalculation();
        if (n > 0  &&  maxWeight > 0) {
            List< List<Integer> > c = new ArrayList<List<Integer>>();
            List<Integer> curr = new ArrayList<Integer>();
            ThreadWorker [] workers;
            ThreadWorker.setItemList(itemList);

            int[] start =  new int [max_thread];
            int[] stop = new int [max_thread];
            int begin = 0;
            int end = maxWeight / max_thread;

            for (int x = 0; x < max_thread - 1; x++){
                start[x] = begin;
                stop[x] = end + begin;
                begin = end + begin + 1;
            }
            start[max_thread - 1] = begin;
            stop[max_thread - 1] = maxWeight;

            c.add(curr);

            for (int j = 0; j <= maxWeight; j++)
                curr.add(0);

            for (int i = 1; i <= n; i++) {
                //System.out.println("---------- Line : " + i + " ----------");
                workers = new ThreadWorker[max_thread];
                List<Integer> prev = curr;
                ThreadWorker.setPrev(prev);
                ThreadWorker.setIdx(i);
                for (int x = 0; x < max_thread; x++){
                    workers[x] = new ThreadWorker(start[x],stop[x], x);
                    workers[x].start();
                    //System.out.println("Thread [" + x +"] working...");
                }
                /*
                * TODO: synchronized is blocking somehow other blocks
                * TODO: Command line argument
                 */
                for (ThreadWorker worker : workers) {
                    try {
                        //System.out.println("[REQ] Thread |" + worker.getID() + "| alive = " + worker.isAlive());
                        worker.join();
                        //System.out.println("[ACK] Thread |" + worker.getID() + "| alive = " + worker.isAlive());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                curr = ThreadWorker.getCurr();
                c.add(curr);
            } // for (i...)

            profit = curr.get(maxWeight);

            for (int i = n, j = maxWeight; i > 0  &&  j >= 0; i--) {
                int tempI   = c.get(i).get(j);
                int tempI_1 = c.get(i-1).get(j);
                if ((i == 0  &&  tempI > 0) || (i > 0  &&  tempI != tempI_1))
                {
                    Item iH = itemList.get(i-1);
                    int  wH = iH.getWeight();
                    iH.setInKnapsack(1);
                    j -= wH;
                    solutionWeight += wH;
                }
            } // for()
            calculated = true;
        } // if()
        return itemList;
    }

    private int getFreeThread(ThreadWorker[] workers) {
        for (int x = 0; x < max_thread; x++){
            if (!workers[x].isAlive()) return x;
        }
        return -1;
    }

    // add an item to the item list
    public void add(String name, int weight, int value) {
        if (name.equals(""))
            name = "" + (itemList.size() + 1);
        itemList.add(new Item(name, weight, value));
        setInitialStateForCalculation();
    }
    // add an item to the item list
    public void add(int weight, int value) {
        add("", weight, value); // the name will be "itemList.size() + 1"!
    }
    // remove an item from the item list
    public void remove(String name) {
        for (Iterator<Item> it = itemList.iterator(); it.hasNext(); ) {
            if (name.equals(it.next().getName())) {
                it.remove();
            }
        }
        setInitialStateForCalculation();
    }
    // remove all items from the item list
    public void removeAllItems() {
        itemList.clear();
        setInitialStateForCalculation();
    }
    public int getProfit() {
        if (!calculated)
            calcSolution();
        return profit;
    }
    public int getSolutionWeight() {return solutionWeight;}
    public boolean isCalculated() {return calculated;}
    public int getMaxWeight() {return maxWeight;}
    public void setMaxWeight(int _maxWeight) {
        maxWeight = Math.max(_maxWeight, 0);
    }
    public void setItemList(List<Item> _itemList) {
        if (_itemList != null) {
            itemList = _itemList;
            for (Item item : _itemList) {
                item.checkMembers();
            }
        }
    }
    // set the member with name "inKnapsack" by all items:
    private void setInKnapsackByAll(int inKnapsack) {
        for (Item item : itemList)
            if (inKnapsack > 0)
                item.setInKnapsack(1);
            else
                item.setInKnapsack(0);
    }
    // set the data members of class in the state of starting the calculation:
    protected void setInitialStateForCalculation() {
        setInKnapsackByAll(0);
        calculated     = false;
        profit         = 0;
        solutionWeight = 0;
    }
} // class
