package ag.ion.bion.officelayer;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class NativeViewHandle {

    /**
     * @member hwnd system window handle
     */
    private final long hwnd;
    /**
     * @member maSystem info about currently used platform
     */
    private final short nativeWindowSystemType;

    public NativeViewHandle(long hwnd, short nativeWindowSystemType) {
        super();
        this.hwnd = hwnd;
        this.nativeWindowSystemType = nativeWindowSystemType;
    }

    public long getHWND() {
        return hwnd;
    }

    public short getNativeWindowSystemType() {
        return nativeWindowSystemType;
    }

    /**
     * @param maHandle system window handle
     * @param maSystem info about currently used platform (see com.sun.star.lang.SystemDependent)
     * @return
     */
    public static Supplier<NativeViewHandle> from(LongSupplier hwnd, IntSupplier nativeWindowSystemType) {
        return () -> new NativeViewHandle( hwnd.getAsLong(), (short) nativeWindowSystemType.getAsInt() );
    }
}
