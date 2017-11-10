package ish.oncourse.willow.editor.webdav

import org.apache.commons.compress.compressors.CompressorOutputStream
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.apache.commons.io.IOUtils

class GzipFile {
    
    private File source
    private File target

    void gzip() throws Exception {
        FileInputStream fis = null
        FileOutputStream fos = null
        CompressorOutputStream gos = null

        try {
            fis = new FileInputStream(source)
            fos = new FileOutputStream(target)

            gos = new CompressorStreamFactory()
                    .createCompressorOutputStream(CompressorStreamFactory.GZIP, fos)
            IOUtils.copy(fis, gos);
        } finally {
            IOUtils.closeQuietly(gos);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(fis);
        }
    }

    static GzipFile valueOf(File source, File target) {
        GzipFile result = new GzipFile()
        result.source = source
        result.target = target
        return result
    }
}
