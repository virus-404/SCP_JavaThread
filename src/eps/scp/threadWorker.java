package eps.scp;

import java.lang.Thread;

class threadWorker  extends Thread{
    private int ID;
    private boolean Stop;

    public threadWorker(int ID){
        this.ID = ID;
        this.Stop = false;
    }

    @Override
    public void run() {

    }

    private int getID() {
        return ID;
    }

}
