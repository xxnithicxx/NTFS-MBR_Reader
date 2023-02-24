package MBR;

public class Partition {
    private int partitionType;
    private long partitionStart;
    private long partitionSize;

    public Partition(int partitionType, long partitionStart, long partitionSize) {
        this.partitionType = partitionType;
        this.partitionStart = partitionStart;
        this.partitionSize = partitionSize;
    }

    public int getPartitionType() {
        return partitionType;
    }

    public long getPartitionStart() {
        return partitionStart;
    }

    public long getPartitionSize() {
        return partitionSize;
    }
}
