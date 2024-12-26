import type {NextConfig} from "next";
import type {Configuration} from "webpack";
import path from "path";

const nextConfig: NextConfig = {
    output: "standalone",
    reactStrictMode: true,
    webpack: (config: Configuration, { isServer }: { isServer: boolean }) => {
        config.resolve = {
            ...config.resolve,
            alias: {
                ...(config.resolve?.alias || {}),
                "@": path.resolve(__dirname, "src"),
            },
            fallback: isServer
                ? config.resolve?.fallback
                : { ...(config.resolve?.fallback || {}), fs: false },
        };

        return config;
    },
    images: {
        remotePatterns: [
            {
                protocol: 'http',
                hostname: 'backend',
                port: '8080',
                pathname: '/images/**',
            },
            {
                protocol: 'http',
                hostname: 'localhost',
                port: '8080',
                pathname: '/images/**',
            },
        ],
    },
};

export default nextConfig;
