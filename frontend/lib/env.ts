import 'server-only'

export function getBackendUrl() {
    const backendUrl = process.env.SPRING_BOOT_API_URL;
    if (!backendUrl) throw new Error('SPRING_BOOT_API_URL is not defined. Please check your .env.local file.');
    return backendUrl;
}