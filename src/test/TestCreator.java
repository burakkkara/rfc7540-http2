package test;

import converter.Converter;
import frame.*;
import http2.HTTP2;

// This class is for test purpose

public class TestCreator {
    enum Flags {
        ACK(1), // ping type
        END_STREAM(1),
        END_HEADERS(4), // Header. frame contains all header block. no CONTINUATION  frame after
        PADDED(8), // (0x8) Both. pad length and padding exist
        PRIORITY(32), // (0x20) Header. e, dependency and weight exist

        ;

        private final int code;

        Flags(int number) {
            this.code = number;
        }

        public int getCode() {
            return this.code;
        }
    }

    enum Type {
        DATA(0),
        HEADERS(1),
        PRIORITY(2),
        RST_STREAM(3),
        SETTINGS(4),
        PUSH_PROMISE(5),
        PING(6),
        GOAWAY(7),
        WINDOW_UPDATE(8),
        CONTINUATION(9);

        private final int type;

        Type(int number) {
            this.type = number;
        }

        public int getType() {
            return this.type;
        }
    }

    enum Errors {
        NO_ERROR(0),
        PROTOCOL_ERROR(1),
        INTERNAL_ERROR(2),
        FLOW_CONTROL_ERROR(3),
        SETTINGS_TIMEOUT(4),
        STREAM_CLOSED(5),
        FRAME_SIZE_ERROR(6),
        REFUSED_STREAM(7),
        CANCEL(8),
        COMPRESSION_ERROR(9),
        CONNECT_ERROR(10),
        ENHANCE_YOUR_CALM(11),
        INADEQUATE_SECURITY(12),
        HTTP_1_1_REQUIRED(13);

        private final int code;

        Errors(int number) {
            this.code = number;
        }

        public int getType() {
            return this.code;
        }
    }

    private void testConverter() {
        Converter converter = new Converter();
        System.out.println(converter.intToBinaryString(32, 0));
        String burak = converter.stringToBinaryString("bur:ak\nkaraonur\nkirm:an");
        System.out.println(burak);
        System.out.println(converter.binaryStringToString(burak));
        System.out.println();
    }

    private void createPacket() {
        HTTP2 packet = new HTTP2();
/*
        // -----------------------------------------------
        Frame headerFrame = packet.getHeader();
        headerFrame.setType(1);
        headerFrame.setFlags(10);
        headerFrame.setR(1);
        headerFrame.setStreamIdentifier(100);

        HeaderFramePayload headerPayload = new HeaderFramePayload();
        headerPayload.setPadLength(10);
        headerPayload.setE(1);
        headerPayload.setDependency(10);
        headerPayload.setWeight(20);
        headerPayload.setHeaders(getHeader());
        headerPayload.setPadding(10);

        headerFrame.setFramePayload(headerPayload);
        headerFrame.setLength(headerPayload.getSize());

        //--------------------------------------------------
        */
    }

    private String getHeader() {
        return "GET / HTTP/2.0\n" +
                "Host: developer.mozilla.org\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:50.0) Gecko/20100101 Firefox/50.0\n" +
                "Accept-Language: en-US,en;q=0.5\n" +
                "Connection: keep-alive\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n";
    }

    public HTTP2 packetExp() {
        HTTP2 packet = new HTTP2();

        Frame settings = new Frame();
        packet.setSettings(settings);
        settings.setType(Type.SETTINGS.getType());
        settings.setFlags(Flags.ACK.getCode());
        settings.setR(0);
        settings.setStreamIdentifier(500);

        SettingFramePayload settingFramePayload = new SettingFramePayload();
        settings.setFramePayload(settingFramePayload);
        settingFramePayload.setValue(0);
        settingFramePayload.setIdentifier(0);
        settings.setLength(settingFramePayload.getSize());

        // HEADER frame---------------------------------------------
        Frame header = new Frame();
        packet.setHeader(header);
        header.setType(Type.HEADERS.getType());
        header.setFlags(Flags.END_HEADERS.getCode());
        header.setR(0); // Must be unset which is 0
        header.setStreamIdentifier(500); // Not sure, random

        HeaderFramePayload headerFramePayload = new HeaderFramePayload();
        header.setFramePayload(headerFramePayload);
        headerFramePayload.setPadLength(0); // not present
        headerFramePayload.setPadding(0); // not present
        headerFramePayload.setWeight(0); // not present
        headerFramePayload.setE(0);  // not present
        headerFramePayload.setDependency(0);  // not present
        headerFramePayload.setHeaders(responseHeader());
        header.setLength(headerFramePayload.getSize());

        // DATA frame----------------------------------------------
        Frame data = new Frame();
        packet.setData(data);
        data.setType(Type.DATA.getType());
        data.setFlags(Flags.END_STREAM.getCode());
        data.setR(0);
        data.setStreamIdentifier(500);

        DataFramePayload dataFramePayload = new DataFramePayload();
        data.setFramePayload(dataFramePayload);
        dataFramePayload.setPadding(0);
        dataFramePayload.setPadLength(0);
        dataFramePayload.setData(responseData());
        data.setLength(dataFramePayload.getSize());

        return packet;
    }

    private String responseHeader() {
        return ":status = 200\n" +
                "content-type = text/plain\n" +
                "content-length = 5000\n";
    }

    public String getResponseHeader() {
        return responseHeader();
    }

    private String responseData() {
        return "burak";
    }
}
