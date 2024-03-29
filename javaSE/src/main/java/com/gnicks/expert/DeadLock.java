package com.gnicks.springboot.study.od;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author gnicks
 * @Date 2023/3/11 01:28
 * @Version 1.8
 */
public class _Expert_DeadLock {

    /**
     * 线程P的需求资源有一个被别人占有，那么就需要等待，不能释放手中的资源
     * 线程Q的需求资源全部都没有被占用，那么无需等待，就要释放手中的所有资源
     * <p>
     * <p>
     * 也就是说实际上是一个多叉有向图，如果图中有环路，那么就造成全局死锁，如果没有环路，那么就没有死锁
     *
     * @param args
     */
    public static void main(String[] args) {


        MyTread[] data = {
                new MyTread(1, new int[]{40}, new int[]{10}),
                new MyTread(2, new int[]{10}, new int[]{30}),
                new MyTread(3, new int[]{30,90}, new int[]{100,40}),
                new MyTread(6, new int[]{20}, new int[]{}),
                new MyTread(10, new int[]{100}, new int[]{80}),
                new MyTread(14, new int[]{80}, new int[]{70,90}),
                new MyTread(19, new int[]{70}, new int[]{60}),
                new MyTread(11, new int[]{60}, new int[]{80}),
                new MyTread(17, new int[]{50}, new int[]{10}),
        };
        _Expert_DeadLock expert_deadLock = new _Expert_DeadLock();
        System.out.println(Arrays.stream(expert_deadLock.getDeadLock(Arrays.asList(data))).boxed().collect(Collectors.toList()));
    }

    public int[] getDeadLock(List<MyTread> threadList) {

        List<Node> vertexes = getVertexes(threadList); // 获取图
        if (vertexes.size() == 0){
            return new int[0];
        }
        return analysisVertexes(vertexes);
    }

    private int[] analysisVertexes(List<Node> vertexes) {
        // DFS 如果有环路，那么该环路相关都为死锁
        Stack<Integer> stack = new Stack<>();
        for (Node node : vertexes) {
            if (!node.isVisited) {
                dfs(node, stack);
            }
        }
        return stack.stream().sorted(Comparator.naturalOrder()).mapToInt(Integer::valueOf).toArray();

    }

    /**
     * 深度搜索 找节点的所有子节点
     * 如果该节点的子节点，有任一子节点的依赖已经出现在了栈里，那么就证明出现回环，那么当前节点也就是死锁中的一环
     * 反之则不具备死锁条件
     *
     * 如果是死锁的情况，那么不需要出栈
     * 如果不是死锁，那么需要出栈
     *
     * @param node
     * @param stack
     * @return
     */
    private boolean dfs(Node node, Stack<Integer> stack) {
        // 修改当前顶点状态 入栈
        stack.push(node.tid);
        node.isVisited = true;

        // 该顶点没有依赖，不具备死锁条件 出栈并通知其父节点我并未找到死锁
        Set<Node> dependencies = node.dependencies;
        if (dependencies == null ){
            stack.pop();
            return false;
        }

        // 标识符 如果子顶点出现死锁，该值将会锁定，锁定该节点为死锁一环
        boolean isDead = false;
        for (Node dependency : dependencies) {
            // 一旦子顶点中出现死锁，直接进行跳过，判定该顶点为死锁一环
            if (isDead){
                return true;
            }

            // 如果当前顶点的子顶点出现在栈里，那么说明搜索路径上跟该顶点的向形成回环
            // 找到死锁了，如果在栈里出现了该元素，那么该元素必定已经访问过了，下方的递归调用将会跳过
            if (stack.contains(dependency.tid)){
                return true; // 有一个依赖形成回环，即可判定该顶点已在死锁环里了，不需要进行下续判断了
            }

            // 如果栈里没有该元素，那么继续深入探索
            if (!dependency.isVisited){
                // 记录递归的状态，如果下方出现死锁环
                 isDead = dfs(dependency, stack);
            }
        }
        // 在返回给递归调用者时，如果没有找到死锁，那么把当前入栈元素弹出
        if (!isDead){stack.pop();}
        return isDead;
    }

    private List<Node> getVertexes(List<MyTread> threadList) {
        Map<Integer, Set<Integer>> ownsPool = new HashMap<>(); // 线程对应的所持资源映射
        for (MyTread myTread : threadList) {
            if (myTread.ownSrc.length > 0){
                ownsPool.put(myTread.id, Arrays.stream(myTread.ownSrc).boxed().collect(Collectors.toSet()));
            }
        }
        List<Node> vertexes = new ArrayList<>();
        fillVertexes(ownsPool, threadList, vertexes); // 填充顶点
        return vertexes.stream().filter(node -> node.dependencies != null).collect(Collectors.toList());
    }

    private void fillVertexes(Map<Integer, Set<Integer>> ownsPool, List<MyTread> threadList, List<Node> vertexes) {
        for (MyTread myTread : threadList) {
            for (Map.Entry<Integer, Set<Integer>> owner : ownsPool.entrySet()) {
                Set<Integer> myNeeds = Arrays.stream(myTread.needSrc).boxed().collect(Collectors.toSet());
                if (myNeeds.isEmpty() ){
                    break;
                }
                myNeeds.retainAll(owner.getValue()); // 取交集
                if (!myNeeds.isEmpty()){
                    aggregate(myTread.id, owner.getKey(), vertexes);
                }
            }
        }

    }

    private void aggregate(int cur, Integer owner, List<Node> vertexes) {
        Node dependency = getVertex(owner, vertexes);
        Node curNode = getVertex(cur, vertexes);
        Set<Node> dependencies = curNode.dependencies;
        if (dependencies == null){
            dependencies = new HashSet<>();
            curNode.dependencies = dependencies;
        }
        dependencies.add(dependency);

    }

    private Node getVertex(int tid, List<Node> vertexes) {
        for (Node vertex : vertexes) {
            if (vertex.tid == tid){
                return vertex;
            }
        }
        Node node = new Node(tid);
        vertexes.add(node);
        return node;
    }


}

class MyTread {

    int id;
    int[] ownSrc;
    int[] needSrc;

    MyTread(int id, int[] ownSrc, int[] needSrc) {
        this.id = id;
        this.ownSrc = ownSrc;
        this.needSrc = needSrc;
    }

}

class Node{
    Set<Node> dependencies;
    int tid;
    boolean isVisited;
    Node(int tid){
        this.tid = tid;
    }

}
