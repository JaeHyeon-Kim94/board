package io.oauth.resourceserverrolesresources.web.page;

public class SimplePageRequest implements Pageable {

    private final int offset;
    private final int size;

    public SimplePageRequest() {
        this(0, 5);
    }

    public SimplePageRequest(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getSize() {
        return size;
    }
}
