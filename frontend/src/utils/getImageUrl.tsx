import {getApiBaseUrl} from '@/utils/getApiBaseUrl';
import {StaticImport} from 'next/dist/shared/lib/get-img-props';

/**
 * Generate a full URL for an image based on its type and filename.
 * @param imageName - The name of the image file or StaticImport.
 * @returns The full URL of the image.
 */
export function getImageUrl(imageName: string | StaticImport | null | undefined): string {
    // ベースURLをメモ化して取得
    const baseUrl = getApiBaseUrl(true);
    // const baseUrl = 'http://backend:8080';

    // 入力が null または undefined の場合、デフォルトのサムネイル画像を返す
    if (!imageName) {
        return '/default-thumbnail.svg';
    }

    // 入力が `StaticImport` 型の場合、`src` を返す（通常 Next.js が対応）
    if (typeof imageName !== 'string') {
        if ('src' in imageName) {
            return imageName.src;
        }
        console.warn('StaticImport does not have src property:', imageName);
        return `${baseUrl}/resources/default-thumbnail.webp`;
    }

    // 文字列の場合、ベースURLに結合
    if (imageName.startsWith('http://') || imageName.startsWith('https://')) {
        // 既に完全なURLの場合はそのまま返す
        return imageName;
    }

    // ログを省略することで不必要なレンダリングトリガーを防ぐ
    return `${baseUrl}${imageName}`;
}
