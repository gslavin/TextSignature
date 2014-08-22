package TextSignature;

/**
 *
 * @author George Slavin
 */
public class TestSignatureExamples {

    public static void main(String[] args) {

        mashupExample();
        
        compareExample();

        summarizeExample();
    }

    /* Creates a Text Signature of two distinct texts
     * and creates a mashup text that follows that syntax patterns of the
     * two texts.
     */
    public static void mashupExample() {
        int wordCount = 100;
        TextSignature text = new TextSignature();
        text.addFileText("sampleTexts/Huck.txt");
        text.addFileText("sampleTexts/Alice.txt");
        System.out.print(text.writeText(wordCount));
    }
    
    /* Compares two texts to see if they are by the same author
     * The larger is used as a reference text and the smaller text signature
     * is compared against the larger.  The error between the two text is
     * returned.
     * error of 1 means complete different
     * error of 0 means same text
     */
    public static void compareExample() {
        TextSignature text1 = new TextSignature();
        TextSignature text2 = new TextSignature();
        text1.addFileText("sampleTexts/LookingGlass.txt");
        text2.addFileText("sampleTexts/Alice.txt");
        System.out.println("Map 1 size: " + text1.getMapSize());
        System.out.println("Map 2 size: " + text2.getMapSize());

        double comp = TextSignature.compareTextSignatures(text1, text2);
        System.out.println(Math.round(100*(1- comp)) + "% match");
    }

    /* Summarizes a passage of Shakespeare using a reference 
     * text of Alice in Wonderland.  Because the shakespeare is so distinct
     * from Alice, the entire text is kept
     */
    public static void summarizeExample() {

        String input1 = "To be, or not to be; that is the bare bodkin That makes calamity ofso long life; For who would fardels bear, till Birnam Wood do come to Dunsinane, But that the fear of something after death Murders the innocent sleep, Great nature's second course, And makes us rather sling"
                + "the arrows of outrageous fortune Than fly to others that we know not of."
                + "There's the respect must give us pause: Wake Duncan with thy knocking! I"
                + "would thou couldst; For who would bear the whips and scorns of time, The"
                + "oppressor's wrong, the proud man's contumely, The law's delay, and the";
        TextSignature refText = new TextSignature();
        refText.addFileText("sampleTexts/Alice.txt");
        System.out.println(refText.summarizeText(input1, 0.9));
    }
}