package cn.com.mustache.multicolor.brackets.provider

import com.intellij.lang.BracePair
import com.jetbrains.php.lang.lexer.PhpTokenTypes

class PHPBracePairProvider : BracePairProvider {
    override fun blackList(): List<BracePair> = listOf(
        BracePair(PhpTokenTypes.PHP_OPENING_TAG, PhpTokenTypes.PHP_CLOSING_TAG, false),
        BracePair(PhpTokenTypes.PHP_ECHO_OPENING_TAG, PhpTokenTypes.PHP_CLOSING_TAG, false)
    )
}