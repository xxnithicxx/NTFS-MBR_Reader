package MBR;

public class Partition {
    private final int partitionType;
    private final long partitionStart;
    private final long partitionSize;

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
