package com.suxiaoshuai.util.system;

import java.lang.management.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代表Java Virtual Machine Implementation的信息。
 * @author sxs
 */
public class JvmInfoUtil {

	/**
	 * 获取基础JVM信息
	 */
	public static Map<String, Object> getJvmBaseInfo() {
		Map<String, Object> info = new LinkedHashMap<>();
		Runtime runtime = Runtime.getRuntime();

		info.put("jvmName", ManagementFactory.getRuntimeMXBean().getVmName());
		info.put("jvmVersion", ManagementFactory.getRuntimeMXBean().getVmVersion());
		info.put("jvmVendor", ManagementFactory.getRuntimeMXBean().getVmVendor());
		info.put("javaVersion", System.getProperty("java.version"));
		info.put("javaHome", System.getProperty("java.home"));
		info.put("pid", getPid());
		info.put("startTime", ManagementFactory.getRuntimeMXBean().getStartTime());
		info.put("upTime", ManagementFactory.getRuntimeMXBean().getUptime() + "ms");

		return info;
	}

	/**
	 * 获取内存信息
	 */
	public static Map<String, String> getMemoryInfo() {
		Map<String, String> memory = new LinkedHashMap<>();
		Runtime runtime = Runtime.getRuntime();
		MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();

		// 堆内存
		MemoryUsage heapUsage = memoryMxBean.getHeapMemoryUsage();
		memory.put("heapUsed", formatSize(heapUsage.getUsed()));
		memory.put("heapCommitted", formatSize(heapUsage.getCommitted()));
		memory.put("heapMax", formatSize(heapUsage.getMax()));

		// 非堆内存
		MemoryUsage nonHeapUsage = memoryMxBean.getNonHeapMemoryUsage();
		memory.put("nonHeapUsed", formatSize(nonHeapUsage.getUsed()));
		memory.put("nonHeapCommitted", formatSize(nonHeapUsage.getCommitted()));

		// 直接内存（需要反射获取）
		try {
			Class<?> c = Class.forName("java.nio.Bits");
			Long maxMemory = (Long) c.getDeclaredMethod("maxMemory").invoke(null);
			Long reservedMemory = (Long) c.getDeclaredMethod("reservedMemory").invoke(null);
			memory.put("directMemoryMax", formatSize(maxMemory));
			memory.put("directMemoryUsed", formatSize(reservedMemory));
		} catch (Exception ignored) {}

		return memory;
	}

	/**
	 * 获取线程信息
	 */
	public static Map<String, Object> getThreadInfo() {
		ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
		Map<String, Object> threads = new LinkedHashMap<>();

		threads.put("threadCount", threadMxBean.getThreadCount());
		threads.put("peakThreadCount", threadMxBean.getPeakThreadCount());
		threads.put("daemonThreadCount", threadMxBean.getDaemonThreadCount());

		// 获取死锁线程
		long[] deadlockedThreads = threadMxBean.findDeadlockedThreads();
		if (deadlockedThreads != null && deadlockedThreads.length > 0) {
			threads.put("deadlockThreads", Arrays.stream(deadlockedThreads)
					.mapToObj(threadMxBean::getThreadInfo)
					.filter(Objects::nonNull)
					.map(ThreadInfo::getThreadName)
					.collect(Collectors.toList()));
		}

		return threads;
	}

	/**
	 * 获取系统信息
	 */
	public static Map<String, Object> getSystemInfo() {
		OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
		Map<String, Object> system = new LinkedHashMap<>();

		system.put("osName", osMxBean.getName());
		system.put("osVersion", osMxBean.getVersion());
		system.put("osArch", osMxBean.getArch());
		system.put("availableProcessors", Runtime.getRuntime().availableProcessors());
		system.put("systemLoad", osMxBean.getSystemLoadAverage());

		return system;
	}

	/**
	 * 获取类加载信息
	 */
	public static Map<String, Object> getClassLoadingInfo() {
		ClassLoadingMXBean classMxBean = ManagementFactory.getClassLoadingMXBean();
		return Map.of(
				"loadedClassCount", classMxBean.getLoadedClassCount(),
				"totalLoadedClassCount", classMxBean.getTotalLoadedClassCount(),
				"unloadedClassCount", classMxBean.getUnloadedClassCount()
		);
	}

	/**
	 * 获取类路径信息
	 */
	public static List<String> getClassPath() {
		return Arrays.asList(System.getProperty("java.class.path").split(":"));
	}

	/**
	 * 获取JVM参数
	 */
	public static Map<String, String> getJvmArguments() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
				.filter(arg -> arg.startsWith("-X"))
				.collect(Collectors.toMap(
						arg -> arg.split("=")[0],
						arg -> arg.contains("=") ? arg.split("=")[1] : ""
				));
	}

	/**
	 * 获取进程ID（兼容JDK8+）
	 */
	public static String getPid() {
		try {
			// JDK9+方式
			return ManagementFactory.getRuntimeMXBean().getPid() + "";
		} catch (UnsupportedOperationException e) {
			// JDK8兼容方式
			String name = ManagementFactory.getRuntimeMXBean().getName();
			return name.split("@")[0];
		}
	}

	/**
	 * 生成汇总报告
	 */
	public static String generateReport() {
		StringBuilder sb = new StringBuilder();
		appendSection(sb, "JVM基本信息", getJvmBaseInfo());
		appendSection(sb, "内存信息", getMemoryInfo());
		appendSection(sb, "线程信息", getThreadInfo());
		appendSection(sb, "系统信息", getSystemInfo());
		appendSection(sb, "类加载信息", getClassLoadingInfo());
		return sb.toString();
	}

	private static void appendSection(StringBuilder sb, String title, Map<?, ?> data) {
		sb.append("\n=== ").append(title).append(" ===\n");
		data.forEach((k, v) -> sb.append(String.format("%-25s: %s%n", k, v)));
	}

	private static String formatSize(long bytes) {
		if (bytes <= 0) return "0";
		String[] units = new String[]{"B", "KB", "MB", "GB"};
		int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
		return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
	}

}
