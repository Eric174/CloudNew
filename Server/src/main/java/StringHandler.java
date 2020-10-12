import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentLinkedDeque;

public class StringHandler extends SimpleChannelInboundHandler<String> {

    static ConcurrentLinkedDeque<Channel> channels =
            new ConcurrentLinkedDeque<>();

    static int cnt = 1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client with ip: " + ctx.channel().localAddress()
                + " connected!");
        channels.add(ctx.channel());
        cnt++;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println("msg from client: " + s);
        channels.forEach(channel -> {
            channel.writeAndFlush("user" + cnt + ": " + s);
        });
    }
}