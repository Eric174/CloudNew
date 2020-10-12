import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;

public class RequestDataEncoder extends MessageToByteEncoder<AbstractMessage> {
    protected void encode(ChannelHandlerContext ctx, AbstractMessage msg, ByteBuf byteBuf) throws Exception {
        if (msg instanceof FileDispatch) {
            FileDispatch dispatch = (FileDispatch) msg;
            File file = new File(dispatch.getName());
            dispatch.setName(file.getName());
            dispatch.setSize(file.length());
            byteBuf.writeLong(dispatch.getSize());
            byteBuf.writeInt(dispatch.getName().length());
            byteBuf.writeCharSequence(dispatch.getName(), Common.charset);
            ChunkedFile chunkedFile = new ChunkedFile(file);
            while (!chunkedFile.isEndOfInput()) {
                byteBuf.writeBytes(chunkedFile.readChunk(ctx.alloc()));
            }
            chunkedFile.close();
            System.out.println("file " + file.getName() + " send full");
        } else {
            throw new Exception("Unknown type message " + msg.getClass());
        }
    }
}
