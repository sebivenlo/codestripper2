
/**
 * A code stripper is a kind of pre configured stream editor that
 * removes marked lines form text files, including source code lines and
 * builds archive from what it understands as source and stripped java/maven projects.
 */

module codestripper {
    requires info.picocli;
    exports codestripper;
}
