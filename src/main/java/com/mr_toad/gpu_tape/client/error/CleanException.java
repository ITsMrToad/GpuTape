package com.mr_toad.gpu_tape.client.error;

import com.mr_toad.lib.mtjava.strings.func.StringSupplier;

public class CleanException extends IllegalStateException {

    public CleanException(StringSupplier msg, Throwable cause) {
        super(msg.getAsString(), cause);
    }

}
