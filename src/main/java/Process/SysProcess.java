package Process;

import java.io.IOException;

public class SysProcess {
    public void  close(String procename) throws IOException {
        String os = System.getProperty("os.name");
        if (os != null && os.toLowerCase().startsWith("windows")) {
           WinProcess.killProc(procename);

        } else if (os != null && os.toLowerCase().startsWith("linux")) {//Linux操作系统
            System.out.println(String.format("当前系统版本是:%s", os));
        } else { //其它操作系统
            System.out.println(String.format("当前系统版本是:%s", os));
        }

    }
}
