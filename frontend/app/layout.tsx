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
      <body className="min-h-screen flex flex-col bg-theme2 text-text">
        <Header />
        <div className="flex-1 p-4">
          <main className="max-w-5xl m-auto">
            {children}
          </main>
        </div>
        <div className="p-4 mt-4 text-center text-xs bg-theme3 border-t border-theme4 shadow-xl">
          <div>
            shadowKO! is not in any way affiliated with The Pokémon Company, Nintendo, Scopely, or Niantic.
          </div>
          <div>
            Pokémon is a trademark of Nintendo. All such trademarks are the property of their respective owners.
          </div>
        </div>
      </body>
    </html>
  );
}
