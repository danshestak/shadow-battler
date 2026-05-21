import Header from "@/components/header/Header";
import "./globals.css";
import { Outfit } from "next/font/google";
import { cn } from "@/lib/utils";

const font = Outfit({subsets:['latin'],variable:'--font-sans'});


export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={cn("font-sans", font.variable)}>
      <body className="min-h-full flex flex-col bg-theme2 text-text">
        <Header />
        <div className="flex-1 p-4">
          <main className="max-w-5xl m-auto">
            {children}
          </main>
        </div>
      </body>
    </html>
  );
}
