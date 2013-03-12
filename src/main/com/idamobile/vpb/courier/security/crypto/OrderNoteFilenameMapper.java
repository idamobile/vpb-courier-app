package com.idamobile.vpb.courier.security.crypto;

import android.content.Context;
import com.idamobile.vpb.courier.util.Files;

import java.io.File;

public class OrderNoteFilenameMapper {

    private Context context;

    public OrderNoteFilenameMapper(Context context) {
        this.context = context;
    }

    public File mapToFileName(int courierId, int orderId) {
        String name = "note-courier-" + courierId + "-order-" +orderId;
        String noteName = Hashs.getSHA1(name);
        return new File(Files.getNotesDir(context), noteName);
    }
}
