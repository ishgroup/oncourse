/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.util;

import java.awt.image.BufferedImage;

public class BackgroundData {
    private final BufferedImage bufferedImage;
    private final boolean landscape;

    public BackgroundData(BufferedImage bufferedImage, boolean landscape) {
        this.bufferedImage = bufferedImage;
        this.landscape = landscape;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public boolean isLandscape() {
        return landscape;
    }
}
