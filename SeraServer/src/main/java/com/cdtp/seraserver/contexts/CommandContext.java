package com.cdtp.seraserver.contexts;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class CommandContext {
    @NonNull
    private String name;
    @NonNull
    private String valuename;
    @NonNull
    private int value;
}
