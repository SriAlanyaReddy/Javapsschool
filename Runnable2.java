public class Runnable2{
//Runnable is functional interface so we can implement them using llamba expressions
    //using join
    public static void main(String[] args) {
        Runnable r1 = () -> {
            for (int i = 0; i < 10; i++)
                System.out.println("Hello manager  " + Thread.currentThread().getName() + ">>" + Thread.currentThread().getId());
        };

        Runnable r2 = () -> {


        for (int i = 0; i < 10; i++)
            System.out.println("Hello employee  " + Thread.currentThread().getName() + ">>" + Thread.currentThread().getId());
    };
        Thread t1=new Thread(r1,"t1");
        System.out.println(t1.getState());
        Thread t2=new Thread(r2,"t2");
        t1.start();
        System.out.println(t1.getState());
        try {
            t1.sleep(1000);//t1.join();

        }
        catch (InterruptedException e){

        }
        t2.start();
        System.out.println(t1.getState());
       ;//t1.start()=>gives illegal exception
        //Thread life cycle has 6
        //states
        //0 new state
        //1 new state
        //2 runnable state
        //3 blocked state
        //4 waiting state(join)
        //5 timed waiting state(sleep)
        //6 terminate

    }
}


