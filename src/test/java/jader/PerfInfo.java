package jader;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;

public record PerfInfo(Duration duration, long allocatedBytes) {

	public static PerfInfo run(Runnable subject, int warmupExecutions) {
		for (int i = 0; i < warmupExecutions; i++) {
			subject.run();
		}
		var threadMXBean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
		var startBytes = threadMXBean.getCurrentThreadAllocatedBytes();
		var start = Instant.now();
		subject.run();
		var stop = Instant.now();
		var stopBytes = threadMXBean.getCurrentThreadAllocatedBytes();
		return new PerfInfo(Duration.between(start, stop), stopBytes - startBytes);
	}

	public static PerfInfo run(Runnable subject) throws Exception {
		return run(subject, 0);
	}

}
