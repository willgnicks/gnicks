

import java.util.Set;

/**
 * @Author gnicks
 * @Date 2023/3/10 23:34
 * @Version 1.8
 */
public class MultiTreeNode {

    public MultiTreeNode parent;
    public int val;
    public Set<MultiTreeNode> children;

    public MultiTreeNode(){}
    public MultiTreeNode(int val){this.val = val;}
    public MultiTreeNode(int val, MultiTreeNode parent){
        this.val = val;
        this.parent = parent;
    }
    public MultiTreeNode(int val, Set<MultiTreeNode> children){
        this.val = val;
        this.children = children;
    }




}
