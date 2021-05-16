package as.set;

public class TreeSet<E extends Comparable<E>> implements ITreeSet<E> {
    final E head;
    final TreeSet<E> left;
    final TreeSet<E> right;

    private TreeSet(E head, TreeSet<E> left, TreeSet<E> right){
        this.head = head;
        this.left = left;
        this.right = right;
    }

    public static <E extends Comparable<E>> ITreeSet<E> empty() {
        return new TreeSet(null, null, null);
    }

    @Override
    public ITreeSet<E> insert(E e) {
        if(e == null) throw new RuntimeException("Cannot insert Null");

        if(this.head == null || !contains(e) && this.head.compareTo(e) < 1) {
            return new TreeSet<>(e, this, null);
        }else if(!contains(e) && this.head.compareTo(e) > 0){
            return new TreeSet<>(e,null,this);
        }else{
            return this;
        }
    }

    @Override
    public boolean contains(E e) {
        if(e.compareTo(head) == 0) return true;

        if(left != null){
            return left.contains(e);
        }if(right != null){
            return right.contains(e);
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return head == null && left == null && right == null;
    }
}
