package io.oauth2.client.web.page;

public class SimplePageRequest implements Pageable {

    private final Long offset;
    private final int size;

    public SimplePageRequest() {
        this(0L, 5);
    }

    public SimplePageRequest(Long offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    @Override
    public Long getOffset() {
        return offset;
    }

    @Override
    public int getSize() {
        return size;
    }
}
