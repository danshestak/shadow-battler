'use client'
import { Menu } from 'lucide-react';
import { buttonVariants } from '../ui/button';
import { cn } from 'cn';

interface HamburgerButtonProps {
  onOpen: () => void;
  isOpen: boolean;
}

const HamburgerButton = ({ onOpen, isOpen }: HamburgerButtonProps) => {
  return (
    <div className="md:hidden">
      <button
        onClick={onOpen}
        className={cn(buttonVariants({ variant: 'blended' }), "h-full aspect-square flex justify-center items-center p-1 rounded-full")}
        aria-label="Open menu"
        aria-expanded={isOpen}
      >
        <Menu className='size-5' />
      </button>
    </div>
  )
}

export default HamburgerButton
