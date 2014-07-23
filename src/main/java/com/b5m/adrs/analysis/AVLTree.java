package com.b5m.adrs.analysis;
import java.util.Iterator;
import java.util.NoSuchElementException;
/** 
 * 平衡二叉树 
 * 定义:首先它是一种特殊的二叉排序树，其次它的左子树和右子树都是平衡二叉树， 
 * 且左子树和右子树的深度之差不超过1 
 * 平衡因子:可以定义为左子树的深度减去右子树的深度 
 *  
 * 平衡二叉树是对二叉排序树的优化，防止二叉排序树在最坏情况下平均查找时间为n， 
 * 二叉排序树在此时形如一个单链表 
 * 平衡二叉树查找元素的次数不超过树的深度，时间复杂度为logN 
 */  
public class AVLTree {  
    /** 
     * 根节点 
     */  
    private Entry root = null;  
      
    /** 
     * 树中元素个数 
     */  
    private int size = 0;  
      
    public AVLTree(){}   
      
    public int size(){  
        return size;  
    }  
  
    /** 
     * @param p 最小旋转子树的根节点 
     * 向左旋转之后p移到p的左子树处，p的右子树B变为此最小子树根节点， 
     * B的左子树变为p的右子树 
     * 比如：     A(-2)                   B(1) 
     *              \        左旋转       /   \ 
     *             B(0)     ---->       A(0)   \        
     *             /   \                   \    \ 
     *           BL(0)  BR(0)              BL(0) BR(0)  
     *  旋转之后树的深度之差不超过1 
     */  
    private void rotateLeft(Entry p) {  
        if(p!=null){  
            Entry r = p.right;  
            p.right = r.left;   //把p右子树的左节点嫁接到p的右节点，如上图，把BL作为A的右子节点  
            if (r.left != null) //如果B的左节点BL不为空，把BL的父节点设为A  
                r.left.parent = p;  
            r.parent = p.parent;  //A的父节点设为B的父节点  
            if (p.parent == null)         //如果p是根节点  
                root = r;                 //r变为父节点，即B为父节点  
            else if (p.parent.left == p)  //如果p是左子节点  
                p.parent.left = r;        //p的父节点的左子树为r  
            else                          //如果p是右子节点  
                p.parent.right = r;       //p的父节点的右子树为r  
            r.left = p;                   //p变为r的左子树，即A为B的左子树  
            p.parent = r;                 //同时更改p的父节点为r，即A的父节点为B  
        }  
    }  
      
    /** 
     * @param p 最小旋转子树的根节点 
     * 向右旋转之后，p移到p的右子节点处，p的左子树B变为最小旋转子树的根节点 
     * B的右子节点变为p的左节点、 
     * 例如:       A(2)                     B(-1) 
     *            /         右旋转          /    \ 
     *          B(0)       ------>         /     A(0) 
     *         /   \                      /      / 
     *       BL(0) BR(0)                BL(0)  BR(0)  
     */  
    private void rotateRight(Entry p){  
        if(p!=null){  
            Entry l = p.left;    
            p.left = l.right;    //把B的右节点BR作为A的左节点  
            if (l.right != null)   //如果BR不为null，设置BR的父节点为A  
                l.right.parent = p;  
            l.parent = p.parent;  //A的父节点赋给B的父节点  
            if (p.parent == null)   //如果p是根节点  
                root = l;          //B为根节点  
            else if (p.parent.right == p) //如果A是其父节点的左子节点  
                p.parent.right = l;     //B为A的父节点的左子树  
            else                        //如果A是其父节点的右子节点  
                p.parent.left = l;      //B为A的父节点的右子树  
            l.right = p;                //A为B的右子树  
            p.parent = l;               //设置A的父节点为B  
        }  
    }  
  
      
    /** 
     * 平衡而二叉树的插入操作 
     * 基本原理为： 
     * 1.首先如同二叉排序树一般，找到其要插入的节点的位置，并把元素插入其中； 
     * 2.自下向上进行回溯，回溯做两个事情： 
     * (1)其一是修改祖先节点的平衡因子，当插入 一个节点时只有根节点到插入节点 
     * 的路径中的节点的平衡因子会被改变，而且改变的原则是当插入节点在某节点(称为A) 
     * 的左子树 中时，A的平衡因子(称为BF)为BF+1,当插入节点在A的右子树中时A的BF-1， 
     * 而判断插入节点在左子树中还是右子树中只要简单的比较它与A的大小 
     * (2)在改变祖先节点的平衡因子的同时，找到最近一个平衡因子大于2或小于-2的节点， 
     * 从这个节点开始调整最小不平衡树进行旋转调整，关于如何调整见下文。 
     * 由于调整后，最小不平衡子树的高度与插入节点前的高度相同，故不需继续要调整祖先节点。 
     * 这里还有一个特殊情况，如果调整BF时，发现某个节点的BF变为0了，则停止向上继续调整， 
     * 因为这表明此节点中高度小的子树增加了新节点，高度不变，那么祖先节点的BF自然不变。 
     *  
     *  
     */  
    public boolean add(Item element){  
        Entry t = root;  
        if(t == null){  
            root = new Entry(element,null);  
            size = 1;  
            return true;  
        }  
        int cmp;  
        Entry parent;  //保存t的父节点  
        //从根节点向下搜索，找到插入位置  
        do {  
            parent = t;       
            cmp = element.getHashCode() - t.element.getHashCode();  
            if(cmp < 0){  
                t = t.left;  
            }else if(cmp > 0){  
                t = t.right;  
            }else{  
                return false;  
            }  
        } while (t!=null);  
          
        Entry child = new Entry(element, parent);  
        if(cmp < 0){  
            parent.left = child;  
              
        }else{  
            parent.right = child;     
        }  
        //自下向上回溯，查找最近不平衡节点  
        while(parent!=null){  
            cmp = element.getHashCode() - parent.element.getHashCode();  
            if(cmp < 0){    //插入节点在parent的左子树中  
                parent.balance++;  
            }else{           //插入节点在parent的右子树中  
                parent.balance--;  
            }  
            if(parent.balance == 0){    //此节点的balance为0，不再向上调整BF值，且不需要旋转  
                break;  
            }  
            if(Math.abs(parent.balance) == 2){  //找到最小不平衡子树根节点  
                fixAfterInsertion(parent);  
                break;                  //不用继续向上回溯  
            }  
            parent = parent.parent;  
        }  
        size ++;  
        return true;  
    }  
      
    /** 
     * 调整的方法： 
     * 1.当最小不平衡子树的根(以下简称R)为2时，即左子树高于右子树： 
     * 如果R的左子树的根节点的BF为1时，做右旋； 
     * 如果R的左子树的根节点的BF为-1时，先左旋然后再右旋 
     *  
     * 2.R为-2时，即右子树高于左子树： 
     * 如果R的右子树的根节点的BF为1时，先右旋后左旋 
     * 如果R的右子树的根节点的BF为-1时，做左旋 
     *  
     * 至于调整之后，各节点的BF变化见代码 
     */  
    private void fixAfterInsertion(Entry p){  
        if(p.balance == 2){  
            leftBalance(p);  
        }  
        if(p.balance == -2){  
            rightBalance(p);  
        }  
    }  
      
      
    /** 
     * 做左平衡处理 
     * 平衡因子的调整如图： 
     *         t                       rd 
     *       /   \                   /    \ 
     *      l    tr   左旋后右旋    l       t 
     *    /   \       ------->    /  \    /  \ 
     *  ll    rd                ll   rdl rdr  tr 
     *       /   \ 
     *     rdl  rdr 
     *      
     *   情况2(rd的BF为0) 
     *  
     *    
     *         t                       rd 
     *       /   \                   /    \ 
     *      l    tr   左旋后右旋    l       t 
     *    /   \       ------->    /  \       \ 
     *  ll    rd                ll   rdl     tr 
     *       /    
     *     rdl   
     *      
     *   情况1(rd的BF为1) 
     *   
     *    
     *         t                       rd 
     *       /   \                   /    \ 
     *      l    tr   左旋后右旋    l       t 
     *    /   \       ------->    /       /  \ 
     *  ll    rd                ll       rdr  tr 
     *           \ 
     *          rdr 
     *      
     *   情况3(rd的BF为-1) 
     *  
     *    
     *         t                         l 
     *       /       右旋处理          /    \ 
     *      l        ------>          ll     t 
     *    /   \                             / 
     *   ll   rd                           rd 
     *   情况4(L等高) 
     */  
    private boolean leftBalance(Entry t){  
        boolean heightLower = true;  
        Entry l = t.left;  
        switch (l.balance) {  
        case LH:            //左高，右旋调整,旋转后树的高度减小  
            t.balance = l.balance = EH;  
            rotateRight(t);  
            break;   
        case RH:            //右高，分情况调整                                            
            Entry rd = l.right;  
            switch (rd.balance) {   //调整各个节点的BF  
            case LH:    //情况1  
                t.balance = RH;  
                l.balance = EH;  
                break;  
            case EH:    //情况2  
                t.balance = l.balance = EH;  
                break;  
            case RH:    //情况3  
                t.balance = EH;  
                l.balance = LH;  
                break;  
            }  
            rd.balance = EH;  
            rotateLeft(t.left);  
            rotateRight(t);  
            break;  
        case EH:      //特殊情况4,这种情况在添加时不可能出现，只在移除时可能出现，旋转之后整体树高不变  
            l.balance = RH;  
            t.balance = LH;  
            rotateRight(t);  
            heightLower = false;  
            break;  
        }  
        return heightLower;  
    }  
      
    /** 
     * 做右平衡处理 
     * 平衡因子的调整如图： 
     *           t                               ld 
     *        /     \                          /     \ 
     *      tl       r       先右旋再左旋     t       r 
     *             /   \     -------->      /   \    /  \ 
     *           ld    rr                 tl   ldl  ldr rr 
     *          /  \ 
     *       ldl  ldr 
     *       情况2(ld的BF为0) 
     *        
     *           t                               ld 
     *        /     \                          /     \ 
     *      tl       r       先右旋再左旋     t       r 
     *             /   \     -------->      /   \       \ 
     *           ld    rr                 tl   ldl      rr 
     *          /   
     *       ldl   
     *       情况1(ld的BF为1) 
     *        
     *           t                               ld 
     *        /     \                          /     \ 
     *      tl       r       先右旋再左旋     t       r 
     *             /   \     -------->      /        /  \ 
     *           ld    rr                 tl        ldr rr 
     *             \ 
     *             ldr 
     *       情况3(ld的BF为-1) 
     *        
     *           t                                  r 
     *             \          左旋                /   \ 
     *              r        ------->           t      rr      
     *            /   \                          \ 
     *           ld   rr                         ld 
     *        情况4(r的BF为0)    
     */  
    private boolean rightBalance(Entry t){  
        boolean heightLower = true;  
        Entry r = t.right;  
        switch (r.balance) {  
        case LH:            //左高，分情况调整  
            Entry ld = r.left;  
            switch (ld.balance) {   //调整各个节点的BF  
            case LH:    //情况1  
                t.balance = EH;  
                r.balance = RH;  
                break;  
            case EH:    //情况2  
                t.balance = r.balance = EH;  
                break;  
            case RH:    //情况3  
                t.balance = LH;  
                r.balance = EH;  
                break;  
            }  
            ld.balance = EH;  
            rotateRight(t.right);  
            rotateLeft(t);  
            break;  
        case RH:            //右高，左旋调整  
            t.balance = r.balance = EH;  
            rotateLeft(t);  
            break;  
        case EH:       //特殊情况4  
            r.balance = LH;  
            t.balance = RH;  
            rotateLeft(t);  
            heightLower = false;  
            break;  
        }  
        return heightLower;  
    }  
      
    /** 
     * 查找指定元素，如果找到返回其Entry对象，否则返回null 
     */  
    protected Entry getEntry(Item element) {    
        Entry tmp = root;    
        int c;    
        while (tmp != null) {    
            c = element.getHashCode() - tmp.element.getHashCode();    
            if (c == 0) {    
                return tmp;    
            } else if (c < 0) {    
                tmp = tmp.left;    
            } else {    
                tmp = tmp.right;    
            }    
        }    
        return null;    
    }
    
    public Item getItem(int hashCode){
    	Entry entry = getEntry(hashCode);
    	if(entry == null) return null;
    	return entry.element;
    }
    
    /** 
     * 查找指定元素，如果找到返回其Entry对象，否则返回null 
     */  
    protected Entry getEntry(int hashCode) {    
        Entry tmp = root;    
        int c;    
        while (tmp != null) {    
            c = hashCode - tmp.element.getHashCode();    
            if (c == 0) {    
                return tmp;    
            } else if (c < 0) {    
                tmp = tmp.left;    
            } else {    
                tmp = tmp.right;    
            }    
        }    
        return null;    
    }    
      
    /** 
     * 平衡二叉树的移除元素操作 
     *  
     */  
    public boolean remove(Item item){  
        Entry e = getEntry(item);  
        if(e!=null){  
            deleteEntry(e);  
            return true;  
        }  
        return false;  
    }  
      
    private void deleteEntry(Entry p){  
        size --;  
        //如果p左右子树都不为空，找到其直接后继，替换p，之后p指向s，删除p其实是删除s  
        //所有的删除左右子树不为空的节点都可以调整为删除左右子树有其一不为空，或都为空的情况。  
        if (p.left != null && p.right != null) {  
             Entry s = successor(p);  
             p.element = s.element;  
             p = s;  
        }  
        Entry replacement = (p.left != null ? p.left : p.right);  
  
        if (replacement != null) {      //如果其左右子树有其一不为空  
            replacement.parent = p.parent;  
            if (p.parent == null)   //如果p为root节点  
                root = replacement;  
            else if (p == p.parent.left)    //如果p是左孩子  
                p.parent.left  = replacement;     
            else                            //如果p是右孩子  
                p.parent.right = replacement;  
  
            p.left = p.right = p.parent = null;     //p的指针清空  
              
            //这里更改了replacement的父节点，所以可以直接从它开始向上回溯  
            fixAfterDeletion(replacement);    
  
        } else if (p.parent == null) { // 如果全树只有一个节点  
            root = null;  
        } else {  //左右子树都为空  
            fixAfterDeletion(p);    //这里从p开始回溯  
            if (p.parent != null) {  
                if (p == p.parent.left)  
                    p.parent.left = null;  
                else if (p == p.parent.right)  
                    p.parent.right = null;  
                p.parent = null;  
            }  
        }     
    }  
      
    /** 
     * 返回以中序遍历方式遍历树时，t的直接后继 
     */  
    static Entry successor(Entry t) {  
        if (t == null)  
            return null;  
        else if (t.right != null) { //往右，然后向左直到尽头  
            Entry p = t.right;  
            while (p.left != null)  
                p = p.left;  
            return p;  
        } else {        //right为空，如果t是p的左子树，则p为t的直接后继  
            Entry p = t.parent;  
            Entry ch = t;  
            while (p != null && ch == p.right) {    //如果t是p的右子树，则继续向上搜索其直接后继  
                ch = p;  
                p = p.parent;  
            }  
            return p;  
        }  
    }  
      
    /** 
     * 删除某节点p后的调整方法： 
     * 1.从p开始向上回溯，修改祖先的BF值，这里只要调整从p的父节点到根节点的BF值， 
     * 调整原则为，当p位于某祖先节点(简称A)的左子树中时，A的BF减1，当p位于A的 
     * 右子树中时A的BF加1。当某个祖先节点BF变为1或-1时停止回溯，这里与插入是相反的， 
     * 因为原本这个节点是平衡的，删除它的子树的某个节点并不会改变它的高度 
     *  
     * 2.检查每个节点的BF值，如果为2或-2需要进行旋转调整，调整方法如下文， 
     * 如果调整之后这个最小子树的高度降低了，那么必须继续从这个最小子树的根节点(假设为B)继续 
     * 向上回溯，这里和插入不一样，因为B的父节点的平衡性因为其子树B的高度的改变而发生了改变， 
     * 那么就可能需要调整，所以删除可能进行多次的调整。 
     *  
     */  
    private void fixAfterDeletion(Entry p){  
        boolean heightLower = true;     //看最小子树调整后，它的高度是否发生变化，如果减小，继续回溯  
        Entry t = p.parent;  
        int cmp;  
        //自下向上回溯，查找不平衡的节点进行调整  
        while(t!=null && heightLower){  
            cmp = p.element.getHashCode() - t.element.getHashCode();  
            /** 
             * 删除的节点是右子树，等于的话，必然是删除的某个节点的左右子树不为空的情况 
             * 例如：     10 
             *          /    \ 
             *         5     15 
             *       /   \ 
             *      3    6  
             * 这里删除5，是把6的值赋给5，然后删除6，这里6是p，p的父节点的值也是6。 
             * 而这也是右子树的一种 
             */   
            if(cmp >= 0 ){     
                t.balance ++;  
            }else{  
                t.balance --;  
            }  
            if(Math.abs(t.balance) == 1){   //父节点经过调整平衡因子后，如果为1或-1，说明调整之前是0，停止回溯。  
                break;  
            }  
            Entry r = t;  
            //这里的调整跟插入一样  
            if(t.balance == 2){  
                heightLower = leftBalance(r);  
            }else if(t.balance==-2){  
                heightLower = rightBalance(r);  
            }  
            t = t.parent;  
        }  
    }  
      
      
      
    private static final int LH = 1;    //左高  
    private static final int EH = 0;    //等高  
    private static final int RH = -1;   //右高  
      
    /** 
     * 树节点，为方便起见不写get，Set方法 
     */  
	static class Entry{  
    	Item element;  
        Entry parent;  
        Entry left;  
        Entry right;  
        int balance = EH;   //平衡因子，只能为1或0或-1  
          
        public Entry(Item element, Entry parent){  
            this.element = element;  
            this.parent = parent;  
        }  
          
        public Entry(){}  
  
        @Override  
        public String toString() {  
            return element+" BF="+balance;  
        }  
              
    }  
      
      
    //返回中序遍历此树的迭代器,返回的是一个有序列表  
    private class BinarySortIterator implements Iterator<Item>{  
        Entry next;  
        Entry lastReturned;  
          
        public BinarySortIterator(){  
            Entry s = root;  
            if(s !=null){  
                while(s.left != null){  //找到中序遍历的第一个元素  
                    s = s.left;  
                }  
            }  
            next = s;   //赋给next  
        }  
          
        @Override  
        public boolean hasNext() {  
            return next!=null;  
        }  
  
        @Override  
        public Item next() {  
            Entry e = next;  
            if (e == null)  
                throw new NoSuchElementException();  
            next = successor(e);  
            lastReturned = e;  
            return e.element;  
        }  
  
        @Override  
        public void remove() {  
            if (lastReturned == null)  
                throw new IllegalStateException();  
            // deleted entries are replaced by their successors  
            if (lastReturned.left != null && lastReturned.right != null)  
                next = lastReturned;  
            deleteEntry(lastReturned);  
            lastReturned = null;  
        }  
    }  
      
    public Iterator<Item> itrator(){  
        return new BinarySortIterator();  
    }  
  
}  