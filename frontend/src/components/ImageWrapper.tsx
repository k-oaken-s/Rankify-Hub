import Image, {ImageProps} from "next/image";
import {useMemo, useState} from "react";


const MAX_RETRY_COUNT = 3;

function ImageWrapper({src, alt, width, height, ...props}: ImageProps) { // widthとheightをpropsとして受け取る
    const [retryCount, setRetryCount] = useState(0);

    const currentSrc = useMemo(() => {
        return retryCount > 0 ? `${src}?retry=${retryCount}` : src;
    }, [src, retryCount]);

    const handleError = () => {
        if (retryCount < MAX_RETRY_COUNT) {
            setRetryCount(retryCount + 1);
        } else {
            // eslint-disable-next-line no-console
            console.error(`Failed to load image after ${MAX_RETRY_COUNT} attempts: ${src}`);
        }
    };

    const imageWidth = width ?? 300;
    const imageHeight = height ?? 300;

    return (
        <Image
            {...props}
            src={currentSrc}
            alt={alt}
            width={imageWidth}
            height={imageHeight}
            onError={handleError}
        />
    );
}

export default ImageWrapper;
