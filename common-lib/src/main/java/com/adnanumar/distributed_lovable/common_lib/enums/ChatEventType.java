package com.adnanumar.distributed_lovable.common_lib.enums;

public enum ChatEventType {

    THOUGHT,        ///  Thoughts for 2s

    MESSAGE,        ///  Standard Conversational text

    FILE_EDIT,      ///  Code generation <file></file>

    TOOL_LOG        /// Reading file... <tool></tool>

}
