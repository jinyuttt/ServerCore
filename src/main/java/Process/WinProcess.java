package Process;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public  class WinProcess{
static Logger log=Logger.getLogger("");

/**
 * @desc 启动进程
 * @author zp
 * @date 2018-3-29
 */
public static void startProc(String processName) {
        log.info("启动应用程序：" + processName);
        if (processName!=null){
        try {
            Desktop.getDesktop().open(new File(processName));
        } catch (Exception e) {
            e.printStackTrace();
           // log.error("应用程序：" + processName + "不存在！");
        }
    }
}
        /**
         * @desc 杀死进程
         * @author zp
         * @throws IOException
         * @date 2018-3-29
         */
        public static void killProc(String processName) throws IOException {
            log.info("关闭应用程序：" + processName);
            if (processName!=null) {
                executeCmd("taskkill /F /IM " + processName);
            }
        }
        /**
         * @desc 执行cmd命令
         * @author zp
         * @date 2018-3-29
         */
        public static String executeCmd(String command) throws IOException {
        log.info("Execute command : " + command);
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("cmd /c " + command);
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
        String line = null;
        StringBuilder build = new StringBuilder();
        while ((line = br.readLine()) != null) {
        log.info(line);
        build.append(line);
        }
        return build.toString();
        }
        /**
         * @desc 判断进程是否开启
         * @author zp
         * @date 2018-3-29
         */
        public static boolean findProcess(String processName) {
            BufferedReader bufferedReader = null;
            try {
                Process proc = Runtime.getRuntime().exec("tasklist -fi " + '"' + "imagename eq " + processName + '"');
                bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(processName)) {
                        return true;
                    }
                }
                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
}

