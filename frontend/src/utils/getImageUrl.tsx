import { StaticImport } from "next/dist/shared/lib/get-img-props";

import { getApiBaseUrl } from "@/utils/getApiBaseUrl";

export function getImageUrl(imageName: string | StaticImport | null | undefined): string {
  const baseUrl = getApiBaseUrl(true);
  // const baseUrl = 'http://backend:8080';

  if (!imageName) {
    return "/default-thumbnail.svg";
  }

  if (typeof imageName !== "string") {
    if ("src" in imageName) {
      return imageName.src;
    }
    console.warn("StaticImport does not have src property:", imageName);
    return `${baseUrl}/resources/default-thumbnail.webp`;
  }

  if (imageName.startsWith("http://") || imageName.startsWith("https://")) {
    return imageName;
  }

  return `${baseUrl}${imageName}`;
}
