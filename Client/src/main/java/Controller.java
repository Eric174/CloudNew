import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ListView<String> listView;
    private final String path = "./Client/data/";
    private EventLoopGroup workerGroup;
    private Channel channel;

    public void initialize(URL location, ResourceBundle resources) {
        refreshList();
        String host = "localhost";
        int port = 8189;
        this.workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RequestDataEncoder(),
                                          new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                          new ClientHandler());
                }
            });

            channel = b.connect(host, port).sync().channel();
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    public void refreshList() {
        File file = new File(path);
        String[] files = file.list();
        listView.getItems().clear();
        if (files.length > 0) {
            for (String name : files) {
                listView.getItems().add(name);
            }
        }
    }

    public void serverList(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) throws Exception {
        String fileName = listView.getSelectionModel().getSelectedItem();
        System.out.println(fileName);
        fileName = path + fileName;
        FileDispatch dispatch = new FileDispatch();
        dispatch.setName(fileName);
        channel.writeAndFlush(dispatch);
    }

    public void refreshList(ActionEvent actionEvent) {
        refreshList();
    }

    public void clientList(ActionEvent actionEvent) {
    }

    public void shutdown() throws InterruptedException {
        // Wait until the connection is closed.
        this.channel.closeFuture().sync();
        this.workerGroup.shutdownGracefully();
    }
}
