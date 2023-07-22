package Process;




import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;


public class ProcessService {

    static Boolean isSomeProcessRun = false;
    Semaphore semaphore = new Semaphore(1);

    public String runProcess(String processName){

        System.out.println(isSomeProcessRun);
        if(isSomeProcessRun){

            return "有程序正在计算，" + processName + " 无法启动";
        }

        try {

            isSomeProcessRun = true;
            semaphore.acquire();
            run(processName);
        }
        catch (InterruptedException e) {

            e.printStackTrace();
        }

        return processName + "后台已调用";
    }

    protected void run(String processName){

        Thread thread = new Thread(){

            @Override
            public void run() {

                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("/root/program/" + processName);
                processBuilder.redirectErrorStream(true);
                try {

                    Process start = processBuilder.start();
                    InputStream inputStream = start.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");

                    char[] chs = new char[1024];
                    int len = -1;
                    while ((len = inputStreamReader.read(chs)) != -1){

                        String str = new String(chs, 0, len);
                        System.out.println(str);
                    }

                    inputStreamReader.close();
                    inputStream.close();
                }
                catch (IOException e) {

                    e.printStackTrace();
                }

                semaphore.release();
                isSomeProcessRun = false;
            }
        };
        thread.start();
    }
}