import { useMemo, useState } from "react";

import Image, { ImageProps } from "next/image";

const MAX_RETRY_COUNT = 3;

type ImageWrapperProps = Omit<ImageProps, "width" | "height"> & {
  width?: number;
  height?: number;
};

function ImageWrapper({ src, alt, width, height, fill, ...props }: ImageWrapperProps) {
  const [retryCount, setRetryCount] = useState(0);

  const currentSrc = useMemo(() => {
    return retryCount > 0 ? `${src}?retry=${retryCount}` : src;
  }, [src, retryCount]);

  const handleError = () => {
    if (retryCount < MAX_RETRY_COUNT) {
      setRetryCount(retryCount + 1);
    } else {
      console.error(`Failed to load image after ${MAX_RETRY_COUNT} attempts: ${src}`);
    }
  };

  // fillが指定されている場合はwidth/heightを省略
  const imageProps = fill
    ? { fill: true }
    : {
        width: width ?? 300,
        height: height ?? 300,
      };

  return <Image {...props} {...imageProps} src={currentSrc} alt={alt} onError={handleError} />;
}

export default ImageWrapper;
