/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.util;

public class ImageRequest {
    private final byte[] pdfContent;
    private boolean a4FormatRequired = false;
    private boolean cutRequired = false;
    private boolean fullBackgroundRequired = false;
    private int qualityScale = 2;

    private ImageRequest(){ pdfContent = null; }

    private ImageRequest(byte[] pdfContent){
        this.pdfContent = pdfContent;
    }


    public boolean isA4FormatRequired() {
        return a4FormatRequired;
    }

    public boolean isCutRequired() {
        return cutRequired;
    }

    public byte[] getPdfContent() {
        return pdfContent;
    }

    public boolean isFullBackgroundRequired() {
        return fullBackgroundRequired;
    }

    public int getQualityScale() {
        return qualityScale;
    }

    public void setQualityScale(int qualityScale) {
        this.qualityScale = qualityScale;
    }

    public static class Builder {

        private final byte[] pdfContent;
        private boolean a4FormatRequired = false;
        private boolean cutRequired = false;
        private boolean fullBackgroundRequired = false;
        private int qualityScale = 2;

        public Builder(byte[] pdfContent) {
            this.pdfContent = pdfContent;
        }

        public Builder cutRequired(boolean cutRequired){
            this.cutRequired = cutRequired;
            return this;
        }

        public Builder a4FormatRequired(boolean a4FormatRequired){
            this.a4FormatRequired = a4FormatRequired;
            return this;
        }

        public Builder fullBackgroundRequired(boolean fullBackgroundRequired){
            this.fullBackgroundRequired = fullBackgroundRequired;
            return this;
        }

        public Builder qualityScale(int qualityScale){
            this.qualityScale = qualityScale;
            return this;
        }

        public ImageRequest build(){
            ImageRequest imageRequest = new ImageRequest(pdfContent);

            imageRequest.a4FormatRequired = a4FormatRequired;
            imageRequest.cutRequired = cutRequired;
            imageRequest.fullBackgroundRequired = fullBackgroundRequired;
            imageRequest.qualityScale = qualityScale;
            return imageRequest;
        }
    }
}
