const config = {
    content: [
        './app/**/*.{js,ts,jsx,tsx,mdx}',
        './pages/**/*.{js,ts,jsx,tsx,mdx}',
        './components/**/*.{js,ts,jsx,tsx,mdx}',
        './src/**/*.{js,ts,jsx,tsx,mdx}',
    ],
    theme: {
        extend: {
            colors: {
                'custom-red': '#F28B82',
                'custom-green': '#81C995',
                'custom-blue': '#AECBFA',
            },
        },
    },
    plugins: [],
}
export default config
