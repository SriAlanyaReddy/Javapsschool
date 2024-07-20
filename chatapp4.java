class chat1{
    boolean flag = false;
    int count =0;
    public synchronized void Question(String msg){
        if(flag){
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }

        }
        System.out.println( Thread.currentThread().getName()+" "+msg);
        flag = true;
        count++;
        notify();
    }
    public synchronized void Answer(String msg){
        if(!flag){
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println(msg);
        count++;
        if(count %4==0){
            flag =false;
            notify();
        }
        //flag =false;

    }
}
class QRunnable implements Runnable{
    chat1 m;
    public QRunnable(chat1 m){
        this.m=m;
    }
    String [] queStrings = {"What is the capital of India ?","Which river is the largest in India ?" };
    public void run(){
        for(int i=0;i<queStrings.length;i++){
            m.Question(queStrings[i]);
        }

    }
}

class ARunnable implements Runnable{
    chat1 m;
    public ARunnable(chat1 m){
        this.m=m;
    }
    String [] ansStrings = {"Delhi","Bombay","NEw Delhi","Delhi","Bombay","NEW delhi" };
    public void run(){
        for(int i=0;i<ansStrings.length;i++){
            m.Answer(ansStrings[i]);
        }
    }
}
class QRunnable1 implements Runnable{
    chat1 m;
    public QRunnable1(chat1 m){
        this.m=m;
    }
    String [] queStrings = {"What is the Largest planet in our solar system?","How many satellites are there for saturn?" };
    public void run(){
        for(int i=0;i<queStrings.length;i++){
            m.Question(queStrings[i]);
        }

    }
}

class ARunnable1 implements Runnable{
    chat1 m;
    public ARunnable1(chat1 m){
        this.m=m;
    }
    String [] ansStrings = {"Jupiter","Saturn","Earth","Jupiter","saturn","earth" };
    public void run(){
        for(int i=0;i<ansStrings.length;i++){
            m.Answer(ansStrings[i]);
        }
    }
}
public class chatapp4 {
    public static void main(String [] args) throws InterruptedException{
        chat1 m= new chat1();
        QRunnable q = new QRunnable(m);
        ARunnable a = new ARunnable(m);
        QRunnable1 q1 = new QRunnable1(m);
        ARunnable1 a1 = new ARunnable1(m);
        Thread question = new Thread(q,"country_bot");
        Thread answer = new Thread(a);
        question.start();
        answer.start();
        question.join();
        answer.join();
        Thread question1 = new Thread(q1,"Universe_Bot");
        Thread answer1 = new Thread(a1);
        question1.start();
        answer1.start();

    }
}