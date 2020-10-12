import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class RequestDecoder extends ReplayingDecoder<AbstractMessage> {
    private final int readBytes = 8192;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        FileDispatch dispatch = new FileDispatch();
        dispatch.setSize(byteBuf.readLong());
        int strLen = byteBuf.readInt();
        dispatch.setName(byteBuf.readCharSequence(strLen, Common.charset).toString());
        //File file = new File(Common.serverPath + dispatch.getName());
        System.out.println(Common.serverPath + dispatch.getName());
        FileOutputStream stream = new FileOutputStream(Common.serverPath + dispatch.getName());
        byte[] bytes = new byte[readBytes];
        long size = dispatch.getSize();
        while (size > 0) {
            if(readBytes > size) {
                bytes = new byte[(int) size];
            }
            byteBuf.readBytes(bytes);
            stream.write(bytes);
            size -= readBytes;
        }
        stream.close();
        System.out.println("File " + dispatch.getName() + " accepted fully");
        list.add(dispatch);
    }
}
