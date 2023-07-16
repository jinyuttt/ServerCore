package monitor;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;
import oshi.hardware.CentralProcessor.TickType;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class OshiUtil {

    public  void  print()
    {
        System.out.println("Initializing System...");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        System.out.println(os);

        System.out.println("Checking computer system...");
        getSys(hal.getComputerSystem());

        System.out.println("Checking Processor...");
        //getCpu(hal.getProcessor());

        System.out.println("Checking Memory...");
        printMemory(hal.getMemory());

        System.out.println("Checking CPU...");
        printCpu(hal.getProcessor());

        System.out.println("Checking Processes...");
      //  printProcesses(os, hal.getMemory());

        System.out.println("Checking Sensors...");
       // printSensors(hal.getSensors());

        System.out.println("Checking Power sources...");
        //printPowerSources(hal.getPowerSources());

        System.out.println("Checking Disks...");
        //printDisks(hal.getDiskStores());

        System.out.println("Checking File System...");
        printFileSystem(os.getFileSystem());

        System.out.println("Checking Network interfaces...");
        printNetworkInterfaces(hal.getNetworkIFs());

        System.out.println("Checking Network parameterss...");
        printNetworkParameters(os.getNetworkParams());

        // hardware: displays
        System.out.println("Checking Displays...");
        //printDisplays(hal.getDisplays());
    }

    public void getSys(final ComputerSystem computerSystem)
    {
        //hal.getComputerSystem();
        //ComputerSystem system1=new ComputerSystem();
        System.out.println("厂商: " + computerSystem.getManufacturer());
        System.out.println("服务器版本: " + computerSystem.getModel());
        System.out.println("序列号: " + computerSystem.getSerialNumber());
        final Firmware firmware = computerSystem.getFirmware();
        System.out.println("firmware:");
        System.out.println("  固件: " + firmware.getManufacturer());
        System.out.println("  名称: " + firmware.getName());
        System.out.println("  描述信息: " + firmware.getDescription());
        System.out.println("  版本: " + firmware.getVersion());
//      System.out.println("  release date: " + (firmware.getReleaseDate() == null ? "unknown": firmware.getReleaseDate() == null ? "unknown" : FormatUtil.formatDate(firmware.getReleaseDate())));
        final Baseboard baseboard = computerSystem.getBaseboard();
        System.out.println("baseboard:");
        System.out.println("  主板厂商: " + baseboard.getManufacturer());
        System.out.println("  主板模块: " + baseboard.getModel());
        System.out.println(" 主板版本: " + baseboard.getVersion());
        System.out.println("  主板序列号: " + baseboard.getSerialNumber());
    }

    public  void  getCpu(CentralProcessor processor)
    {
        System.out.println(processor);
        System.out.println(" " + processor.getPhysicalPackageCount() + " physical CPU package(s)");
        System.out.println(" " + processor.getPhysicalProcessorCount() + " physical CPU core(s)");
        System.out.println(" " + processor.getLogicalProcessorCount() + " logical CPU(s)");

        System.out.println("Identifier: " + processor.getProcessorIdentifier());
        System.out.println("ProcessorID: " + processor.getProcessorIdentifier().getProcessorID());
    }

    private static void printMemory(GlobalMemory memory) {
        System.out.println("内存总量: "
                + FormatUtil.formatBytes(memory.getTotal()));
        System.out.println("内存剩余: "
                + FormatUtil.formatBytes(memory.getAvailable()));
//        System.out.println("Memory: " + FormatUtil.formatBytes(memory.getAvailable()) + "/"
//                + FormatUtil.formatBytes(memory.getTotal()));
        System.out.println("Swap used: " + FormatUtil.formatBytes(memory.getVirtualMemory().getSwapUsed()) + "/"
                + FormatUtil.formatBytes(memory.getVirtualMemory().getSwapTotal()));
    }

    private static void printCpu(CentralProcessor processor) {
        System.out.println(
                "Context Switches/Interrupts: " + processor.getContextSwitches() + " / " + processor.getInterrupts());

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        System.out.println("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        System.out.println("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long sys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;

        System.out.format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%%n",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu);
        System.out.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100);
        //System.out.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
        double[] loadAverage = processor.getSystemLoadAverage(3);
        System.out.println("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        System.out.println("cpu核数:" + processor.getLogicalProcessorCount());

        System.out.println("cpu系统使用率:" + new DecimalFormat("#.##%").format(sys * 1.0 / totalCpu));

        System.out.println("cpu用户使用率:" + new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));

        System.out.println("cpu当前等待率:" + new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));

        System.out.println("cpu当前使用率:" + new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu)));
        // per core CPU
//      StringBuilder procCpu = new StringBuilder("CPU load per processor:");
//      double[] load = processor.getProcessorCpuLoadBetweenTicks();
//      for (double avg : load) {
//          procCpu.append(String.format(" %.1f%%", avg * 100));
//      }
//      System.out.println(procCpu.toString());
    }

    private static void printFileSystem(FileSystem fileSystem) {
        System.out.println("File System:");

        System.out.format(" File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors());

        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            System.out.format(
                    " %s (%s) [%s] %s of %s free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s%n",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount());
        }
    }

    private static void printNetworkInterfaces(List<NetworkIF> list) {
        System.out.println("Network interfaces:");
        for (NetworkIF net : list) {
            System.out.format(" Name: %s (%s)%n", net.getName(), net.getDisplayName());
            System.out.format("   MAC Address: %s %n", net.getMacaddr());
            System.out.format("   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps"));
            System.out.format("   IPv4: %s %n", Arrays.toString(net.getIPv4addr()));
            System.out.format("   IPv6: %s %n", Arrays.toString(net.getIPv6addr()));
            boolean hasData = net.getBytesRecv() > 0 || net.getBytesSent() > 0 || net.getPacketsRecv() > 0
                    || net.getPacketsSent() > 0;
            System.out.format("   Traffic: received %s/%s%s; transmitted %s/%s%s %n",
                    hasData ? net.getPacketsRecv() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesRecv()) : "?",
                    hasData ? " (" + net.getInErrors() + " err)" : "",
                    hasData ? net.getPacketsSent() + " packets" : "?",
                    hasData ? FormatUtil.formatBytes(net.getBytesSent()) : "?",
                    hasData ? " (" + net.getOutErrors() + " err)" : "");
        }
    }

    private static void printNetworkParameters(NetworkParams networkParams) {
        System.out.println("Network parameters:");
        System.out.format(" Host name: %s%n", networkParams.getHostName());
        System.out.format(" Domain name: %s%n", networkParams.getDomainName());
        System.out.format(" DNS servers: %s%n", Arrays.toString(networkParams.getDnsServers()));
        System.out.format(" IPv4 Gateway: %s%n", networkParams.getIpv4DefaultGateway());
        System.out.format(" IPv6 Gateway: %s%n", networkParams.getIpv6DefaultGateway());
    }

}
