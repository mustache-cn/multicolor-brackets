package cn.com.mustache.multicolor.brackets


val brackets = MulticolorHighlighter.getBrackets()

fun CharSequence.toChar() = elementAt(0)

fun roundLevel(level: Int) = MulticolorHighlighter.getMulticolorColor(MulticolorHighlighter.NAME_ROUND_BRACKETS, level)

fun squigglyLevel(level: Int) = MulticolorHighlighter.getMulticolorColor(MulticolorHighlighter.NAME_SQUIGGLY_BRACKETS, level)

fun angleLevel(level: Int) = MulticolorHighlighter.getMulticolorColor(MulticolorHighlighter.NAME_ANGLE_BRACKETS, level)

fun squareLevel(level: Int) = MulticolorHighlighter.getMulticolorColor(MulticolorHighlighter.NAME_SQUARE_BRACKETS, level)