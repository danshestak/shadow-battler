'use client'
import HeaderButton from '../layout/HeaderButton'
import { X } from 'lucide-react';
import { buttonVariants } from '../ui/button';
import { cn } from 'cn';

interface SidebarProps {
  buttons: string[];
  isOpen: boolean;
  onClose: () => void;
}

const Sidebar = (p: SidebarProps) => {
  return (
    <>
      <div
        className={`fixed inset-0 z-20 md:hidden
            bg-theme1/75 backdrop-blur-xs
            transition-opacity duration-300 ease-in-out ${p.isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}
        onClick={p.onClose}
        aria-hidden="true"
      />

      <div
        className={`fixed top-0 right-0 h-full w-1/2 p-2 z-30 md:hidden
            bg-theme1 border-l border-theme4 shadow-lg 
            transform transition-transform duration-300 ease-in-out ${p.isOpen ? 'translate-x-0' : 'translate-x-full'}`}
        role="dialog"
        aria-modal="true"
        aria-labelledby="sidebar-title"
      >
        <div className="flex flex-col justify-end gap-2">
          <div className='h-12 flex justify-end pr-2'>
            <button
              onClick={p.onClose}
              className={cn(buttonVariants({ variant: 'blended' }), "h-full aspect-square flex justify-center items-center p-1 rounded-full")}>
              <X className='size-5'/>
            </button>
          </div>
          {p.buttons.map(s => <HeaderButton key={s} content={s} onClick={p.onClose} />)}
        </div>
      </div>
    </>
  )
}

export default Sidebar