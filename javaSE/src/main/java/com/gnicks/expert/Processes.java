

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author gnicks
 * @Date 2023/3/10 23:39
 * @Version 1.8
 */
public class Processes {
    private static String[] specialInfo = {};
    private final static String SYMBOL = "START";
    private final static String ZERO_STARTER = "0";
    private final static int VALID_MIN = 1;
    private final static int VALID_MAX = 9999;

    public static void main(String[] args) {
        String[] logs = {
                "2022-02-30 15:00:00|WARN|5|START 1239", // 5 -> 1239
                "2022-02-30 15:00:00|INFO|1234|Programme started", // 1234
                "2022-02-30 15:00:00|WARN|1234|START 5", // 1234 -> 5
                "2022-02-30 15:00:00|INFO|3309|Programme started", // 3309
                "2022-02-30 15:00:00|INFO|1990|logs.", // 1990
                "2022-02-30 15:00:00|INFO|1234|START 1250", // 1234 -> 1250
                "2022-02-30 15:00:00|INFO|1234|START 0250", // 无效
                "2022-02-30 15:00:00|INFO|1234|START -250", // 无效
                "2022-02-30 15:00:00|INFO|1234|Start 1250", // 无效
                "2022-02-30 15:00:00|INFO|1234|START 1900", // 1234 -> 1900
                "2022-02-30 15:00:00|INFO|1234|START 121", // 1234 -> 121
                "2022-02-30 15:00:00|INFO|3309|START 55", // 3309 -> 55
                "2022-02-30 15:00:00|INFO|99|Programme started" // 99
        };

        System.out.println(getDepthProcesses(logs, 2));
    }

    /**
     * log中第四部分为子进程日志，如果START sub_processId，那么该id范围[1-9999]
     * 并且START开头 进程号不以0开头
     * start 1234
     * START -12
     * START 02
     * 均是不符合规则
     *
     * @param logs
     * @param depth
     * @return
     */
    private static int getDepthProcesses(String[] logs, int depth) {
        if (depth < 1){
            return 0;
        }
        List<MultiTreeNode> roots = getRootProcess(logs);
        if (depth == 1) {
            return (int) roots.stream().count();
        }
        return getDeepCount(roots, depth);

    }

    /**
     * 广搜一层
     *
     * @param roots
     * @param depth
     * @return
     */
    private static int getDeepCount(List<MultiTreeNode> roots, int depth) {

        boolean isReachDepth;
        int result = 0;
        int currentDepth = 1;
        Deque<MultiTreeNode> deque = new ArrayDeque<>(roots);
        while (!deque.isEmpty()) {
            isReachDepth = ++currentDepth == depth;
            int size = deque.size();
            while (size > 0) {
                size--;
                MultiTreeNode node = deque.poll();
                Set<MultiTreeNode> children = node.children;
                if (children == null) {
                    continue;
                }
                if (isReachDepth) {
                    result += children.size();
                    continue;
                }
                Iterator<MultiTreeNode> iterator = children.iterator();
                while (iterator.hasNext()) {
                    deque.offer(iterator.next());
                }
            }
            if (isReachDepth) {
                break;
            }
        }
        return result;

    }

    private static List<MultiTreeNode> getRootProcess(String[] logs) {
        List<MultiTreeNode> processes = new ArrayList<>();
        for (int i = 0; i < logs.length; i++) {
            String[] log = logs[i].split("\\|");
            aggregate(log[3], Integer.parseInt(log[2]), processes);
        }

        return processes.stream().filter(node -> node.parent == null).collect(Collectors.toList());
    }

    /**
     * @param subInfo
     * @param parentId
     */
    private static void aggregate(String subInfo, int parentId, List<MultiTreeNode> processes) {
        MultiTreeNode parent = getProcess(processes, parentId);
        int subId = isValidSubProcess(subInfo);
        if (subId < VALID_MIN || subId > VALID_MAX) {   // 子进程无效若是无效不关联
            return;
        }
        MultiTreeNode child = getProcess(processes, subId);
        Set<MultiTreeNode> children = parent.children;
        if (children == null) {
            children = new HashSet<>();
            parent.children = children; // 关联父进程的子进程
        }
        child.parent = parent; // 关联子进程的父进程
        children.add(child);
    }

    private static MultiTreeNode getProcess(List<MultiTreeNode> processes, int val) {
        for (MultiTreeNode process : processes) {
            if (process.val == val) {
                return process;
            }
        }
        MultiTreeNode node = new MultiTreeNode(val);
        processes.add(node);
        return node;
    }

    private static int isValidSubProcess(String subInfo) {
        String[] sub = subInfo.split(" ");
        if (sub.length != 2) {
            return 0;
        }
        if (!SYMBOL.equals(sub[0])) {
            return 0;
        }
        String subId = sub[1];
        // 不是纯数字，或者数字以-开头，以0开头都是无效的
        if (subId.startsWith(ZERO_STARTER) || !subId.matches("\\d+")) {
            return 0;
        }
        return Integer.parseInt(subId);

    }


    private static boolean isParentOnly(String subInfo) {
        // 没有数字且不是特殊的字符
        return !Arrays.asList(specialInfo).contains(subInfo)
                && !subInfo.matches(".*[0-9]+.*");
    }

}
