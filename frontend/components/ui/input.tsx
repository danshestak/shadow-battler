import * as React from "react"
import { Input as InputPrimitive } from "@base-ui/react/input"

import { cn } from "@/lib/utils"

function Input({ className, type, ...props }: React.ComponentProps<"input">) {
  return (
    <InputPrimitive
      type={type}
      data-slot="input"
      className={cn(
        "w-full min-w-0 rounded border border-input bg-transparent py-1 px-2 transition-colors outline-none file:inline-flex file:h-6 file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-text/50 focus-visible:border-text disabled:pointer-events-none disabled:cursor-not-allowed aria-invalid:border-highlight",
        className
      )}
      {...props}
    />
  )
}

export { Input }
