package ws;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class FJMS extends RecursiveAction {
    public final int[] is, tmp;
    private final int l, r;


    public FJMS(int[] is, int[] tmp, int l, int r){
        this.is = is;
        this.tmp = tmp;
        this.l = l;
        this.r = r;
    }

    protected void compute(){
        if(r - l <= 100_000) Arrays.sort(elems, l, r);
        else{
            int mid = (l+r)/2;
            FJMS left = new FJMS(is, tmp, l, mid);
            FJMS right = new FJMS(is, tmp, mid, r);
            left.fork();
            right.invoke();
            left.join();
            merge(is, tmp, l, mid, r);
        }
    }
    private static void merge(int[] elem, int[] tmp, int leftPos, int rightPos, int rightEnd) {
        if (elem[rightPos - 1] <= elem[rightPos]) return;

        int leftEnd = rightPos;
        int tmpPos = leftPos;
        int numElements = rightEnd - leftPos;

        while (leftPos < leftEnd && rightPos < rightEnd)
            if (elem[leftPos] <= elem[rightPos])
                tmp[tmpPos++] = elem[leftPos++];
            else
                tmp[tmpPos++] = elem[rightPos++];

        while (leftPos < leftEnd)
            tmp[tmpPos++] = elem[leftPos++];

        while (rightPos < rightEnd)
            tmp[tmpPos++] = elem[rightPos++];

        rightEnd--;
        for (int i = 0; i < numElements; i++, rightEnd--)
            elem[rightEnd] = tmp[rightEnd];
    }
}
